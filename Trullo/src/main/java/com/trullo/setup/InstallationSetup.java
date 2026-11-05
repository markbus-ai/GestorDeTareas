package com.trullo.setup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/** Prepara la configuracion y la base PostgreSQL privada de una instalacion de Trullo. */
public final class InstallationSetup {
    public static final int DEFAULT_PORT = 54329;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<String> PRIVATE_KEYS = List.of(
            "WHATSAPP_API_URL", "WHATSAPP_API_ZONE", "API_KEY",
            "SMTP_HOST", "SMTP_PORT", "SMTP_USER", "SMTP_PASSWORD", "SMTP_FROM");
    private static final Set<String> ALLOWED_PRIVATE_KEYS = Set.copyOf(PRIVATE_KEYS);
    private static final SecureRandom RANDOM = new SecureRandom();

    private InstallationSetup() {
    }

    /** Carpetas de configuracion y datos del usuario actual. */
    public record UserPaths(Path config, Path data) {
    }

    /** Estado persistente del cluster privado. */
    public record DatabaseState(int format, int port, String adminPassword, String appPassword) {
    }

    /** Binarios de PostgreSQL usados por la instalacion. */
    public record PostgresBinaries(Path initdb, Path pgCtl, Path psql) {
    }

    /** Bloqueo que impide dos instancias simultaneas. */
    public static final class SetupLock implements AutoCloseable {
        private final FileChannel channel;
        private final FileLock lock;

        private SetupLock(FileChannel channel, FileLock lock) {
            this.channel = channel;
            this.lock = lock;
        }

        @Override
        public void close() throws IOException {
            try {
                lock.release();
            } finally {
                channel.close();
            }
        }
    }

    /** Devuelve las carpetas de configuracion y datos para el sistema actual. */
    public static UserPaths userPaths() {
        Path home = Path.of(System.getProperty("user.home"));
        if (isWindows()) {
            Path appData = envPath("APPDATA", home.resolve("AppData/Roaming"));
            Path localData = envPath("LOCALAPPDATA", home.resolve("AppData/Local"));
            return new UserPaths(appData.resolve("Trullo"), localData.resolve("Trullo"));
        }
        return new UserPaths(xdgPath("XDG_CONFIG_HOME", home.resolve(".config")).resolve("trullo"),
                xdgPath("XDG_DATA_HOME", home.resolve(".local/share")).resolve("trullo"));
    }

    /** Detecta la carpeta de la aplicacion cuando se ejecuta desde un app-image de jpackage. */
    public static Path installationDirectory() {
        String configured = System.getenv("TRULLO_HOME");
        if (configured != null && !configured.isBlank()) {
            return Path.of(configured).toAbsolutePath().normalize();
        }
        Path runtime = Path.of(System.getProperty("java.home")).toAbsolutePath().normalize();
        Path parent = runtime.getParent();
        if (parent != null && "lib".equals(String.valueOf(parent.getFileName()))) {
            Path application = parent.getParent();
            if (application != null) {
                return application;
            }
        }
        if (parent != null && "runtime".equals(String.valueOf(runtime.getFileName()))) {
            return parent;
        }
        return Path.of("").toAbsolutePath().normalize();
    }

    /** Lee un archivo de variables simple sin interpretar el entorno del proceso. */
    public static Map<String, String> readEnv(Path file) throws IOException {
        Map<String, String> values = new LinkedHashMap<>();
        if (file == null || !Files.isRegularFile(file)) {
            return values;
        }
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.isBlank() || line.stripLeading().startsWith("#") || !line.contains("=")) {
                continue;
            }
            int separator = line.indexOf('=');
            String key = line.substring(0, separator).strip();
            String value = line.substring(separator + 1).strip();
            if (value.length() >= 2 && value.charAt(0) == value.charAt(value.length() - 1)
                    && (value.charAt(0) == '"' || value.charAt(0) == '\'')) {
                value = value.substring(1, value.length() - 1);
            }
            values.put(key, value);
        }
        return values;
    }

    /** Lee la configuracion privada que el administrador incluyo al compilar. */
    public static Map<String, String> privateValues(Path installation) throws IOException {
        Map<String, String> source = readEnv(installation.resolve("setup/private.env"));
        Map<String, String> values = new LinkedHashMap<>();
        for (String key : PRIVATE_KEYS) {
            String value = source.get(key);
            if (value == null) {
                continue;
            }
            if (value.indexOf('"') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
                throw new IllegalStateException("Configuracion privada invalida en " + key);
            }
            values.put(key, value);
        }
        if (values.getOrDefault("WHATSAPP_API_URL", "").isBlank()
                || values.getOrDefault("API_KEY", "").isBlank()) {
            throw new IllegalStateException("El paquete no incluye WHATSAPP_API_URL y API_KEY.");
        }
        values.putIfAbsent("WHATSAPP_API_ZONE", "America/Argentina/Buenos_Aires");
        for (String key : values.keySet()) {
            if (!ALLOWED_PRIVATE_KEYS.contains(key)) {
                throw new IllegalStateException("Clave privada no permitida: " + key);
            }
        }
        return values;
    }

    /** Corta la preparacion si otra instancia de Trullo ya esta abierta. */
    public static SetupLock lock(Path data) throws IOException {
        Files.createDirectories(data);
        privateDirectory(data);
        FileChannel channel = FileChannel.open(data.resolve("setup.lock"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        FileLock lock;
        try {
            lock = channel.tryLock();
        } catch (OverlappingFileLockException error) {
            lock = null;
        }
        if (lock == null) {
            channel.close();
            throw new IllegalStateException("Trullo ya esta abierto. Cerralo antes de preparar la base.");
        }
        return new SetupLock(channel, lock);
    }

    /** Devuelve el estado persistente, generando contrasenas nuevas solo en la primera ejecucion. */
    public static DatabaseState databaseState(Path data, int port) throws IOException {
        Files.createDirectories(data);
        Path path = data.resolve("database-state.json");
        if (Files.exists(path)) {
            JsonObject state = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            if (state.get("format").getAsInt() != 1) {
                throw new IllegalStateException("Formato de configuracion de base no compatible.");
            }
            return new DatabaseState(1, state.get("port").getAsInt(),
                    state.get("admin_password").getAsString(), state.get("app_password").getAsString());
        }
        if (Files.exists(data.resolve("postgres/PG_VERSION"))) {
            throw new IllegalStateException(
                    "Existe una base sin su configuracion. No se modifico; restaura database-state.json.");
        }
        DatabaseState state = new DatabaseState(1, port, randomPassword(), randomPassword());
        JsonObject json = new JsonObject();
        json.addProperty("format", state.format());
        json.addProperty("port", state.port());
        json.addProperty("admin_password", state.adminPassword());
        json.addProperty("app_password", state.appPassword());
        writePrivate(path, GSON.toJson(json) + "\n");
        return state;
    }

    /** Localiza initdb, pg_ctl y psql en la instalacion o en el sistema. */
    public static PostgresBinaries postgresBinaries(Path installation, Path override) {
        Path directory;
        if (override != null) {
            directory = override;
        } else if (isWindows()) {
            directory = installation.resolve("postgres/bin");
        } else {
            Path pgConfig = findOnPath("pg_config");
            if (pgConfig != null) {
                try {
                    directory = Path.of(runCapture(List.of(pgConfig.toString(), "--bindir"), null, Map.of()).strip());
                } catch (IOException error) {
                    throw new IllegalStateException("No se pudo consultar pg_config.", error);
                }
            } else {
                Path initdb = findOnPath("initdb");
                if (initdb != null) {
                    directory = initdb.getParent();
                } else {
                    directory = latestSystemPostgres();
                }
            }
        }
        String suffix = isWindows() ? ".exe" : "";
        PostgresBinaries binaries = new PostgresBinaries(
                directory.resolve("initdb" + suffix),
                directory.resolve("pg_ctl" + suffix),
                directory.resolve("psql" + suffix));
        if (!Files.isRegularFile(binaries.initdb())
                || !Files.isRegularFile(binaries.pgCtl())
                || !Files.isRegularFile(binaries.psql())) {
            throw new IllegalStateException("No se encuentran todos los binarios de PostgreSQL.");
        }
        return binaries;
    }

    /** Inicializa, arranca y prepara la base privada; devuelve el estado persistente. */
    public static DatabaseState prepareDatabase(Path installation, Path data,
                                                PostgresBinaries binaries, int port) throws IOException {
        DatabaseState state = databaseState(data, port);
        Path cluster = data.resolve("postgres");
        if (!Files.isRegularFile(cluster.resolve("PG_VERSION"))) {
            Files.createDirectories(data);
            Path password = Files.createTempFile(data, ".pg-pass-", ".txt");
            try {
                writePrivate(password, state.adminPassword() + "\n");
                runCapture(List.of(binaries.initdb().toString(), "-D", cluster.toString(),
                        "-U", "trullo_admin", "--auth=scram-sha-256", "--encoding=UTF8",
                        "--no-locale", "--pwfile=" + password), data, Map.of());
            } finally {
                Files.deleteIfExists(password);
            }
            String socket = isWindows() ? "" : cluster.toString().replace("'", "''");
            Files.writeString(cluster.resolve("postgresql.conf"),
                    "\nlisten_addresses = '127.0.0.1'\nport = " + state.port()
                            + "\nunix_socket_directories = '" + socket + "'\n",
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        }
        if (!pgStatus(binaries, cluster)) {
            runCapture(List.of(binaries.pgCtl().toString(), "-D", cluster.toString(),
                    "-l", data.resolve("postgres.log").toString(), "-w", "-t", "30", "start"),
                    data, Map.of());
        }

        Map<String, String> env = new HashMap<>();
        env.put("PGHOST", "127.0.0.1");
        env.put("PGPORT", String.valueOf(state.port()));
        env.put("PGUSER", "trullo_admin");
        env.put("PGPASSWORD", state.adminPassword());
        env.put("PGCONNECT_TIMEOUT", "5");
        env.put("PGCLIENTENCODING", "UTF8");

        if (!"1".equals(psql(binaries, env, "postgres",
                "SELECT 1 FROM pg_roles WHERE rolname='trullo_app';"))) {
            psql(binaries, env, "postgres",
                    "CREATE ROLE trullo_app LOGIN PASSWORD '" + state.appPassword() + "';");
        }
        if (!"1".equals(psql(binaries, env, "postgres",
                "SELECT 1 FROM pg_database WHERE datname='trullo';"))) {
            psql(binaries, env, "postgres", "CREATE DATABASE trullo OWNER trullo_app;");
        }
        String marker = psql(binaries, env, "trullo",
                "SELECT to_regclass('public.trullo_schema_version');");
        if (marker.isBlank()) {
            if (!"0".equals(psql(binaries, env, "trullo",
                    "SELECT count(*) FROM pg_tables WHERE schemaname='public';"))) {
                throw new IllegalStateException(
                        "La base tiene tablas ajenas al instalador. No se modifico ninguna tabla.");
            }
            String enums = Files.readString(installation.resolve("setup/sql/enums.sql"), StandardCharsets.UTF_8);
            String schema = Files.readString(installation.resolve("setup/sql/schema.sql"), StandardCharsets.UTF_8);
            schema = String.join("\n", schema.lines()
                    .filter(line -> !line.stripLeading().toLowerCase().startsWith("create database"))
                    .toList());
            psql(binaries, env, "trullo", "BEGIN; SET ROLE trullo_app;\n" + enums + "\n" + schema
                    + "\nCREATE TABLE trullo_schema_version(version integer PRIMARY KEY);"
                    + "INSERT INTO trullo_schema_version VALUES (1); COMMIT;");
        } else if (!"1".equals(psql(binaries, env, "trullo",
                "SELECT version FROM trullo_schema_version;"))) {
            throw new IllegalStateException("Version del schema no compatible. No se modifico la base.");
        }
        return state;
    }

    /** Detiene solo el cluster privado, nunca el servicio PostgreSQL del sistema. */
    public static void stopPostgres(Path data, PostgresBinaries binaries) {
        Path cluster = data.resolve("postgres");
        if (!Files.isRegularFile(cluster.resolve("PG_VERSION"))) {
            return;
        }
        try {
            new ProcessBuilder(binaries.pgCtl().toString(), "-D", cluster.toString(),
                    "-m", "fast", "-w", "stop")
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start()
                    .waitFor();
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        } catch (IOException ignored) {
            // El cluster puede haber quedado detenido por otra ejecucion.
        }
    }

    /** Escribe la configuracion del usuario conservando valores y credenciales existentes. */
    public static void writeConfig(Path config, DatabaseState state,
                                   Map<String, String> privateValues) throws IOException {
        Files.createDirectories(config);
        privateDirectory(config);
        Path path = config.resolve(".env");
        List<String> lines = new ArrayList<>();
        if (Files.exists(path)) {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String trimmed = line.stripLeading();
                if (trimmed.startsWith("UV_EXECUTABLE=") || trimmed.startsWith("WHATSPLAY_DIR=")) {
                    continue;
                }
                lines.add(line);
            }
        } else {
            lines.add("DB_HOST=127.0.0.1");
            lines.add("DB_PORT=" + state.port());
            lines.add("DB_NAME=trullo");
            lines.add("DB_USER=trullo_app");
            lines.add("DB_PASSWORD=\"" + state.appPassword() + "\"");
            lines.add("");
            lines.add("# Recordatorios: cada canal se activa solo si esta configurado.");
        }
        for (String key : PRIVATE_KEYS) {
            String value = privateValues.get(key);
            if (value == null || hasKey(lines, key)) {
                continue;
            }
            lines.add(key + "=\"" + value + "\"");
        }
        writePrivate(path, String.join("\n", lines) + "\n");
    }

    private static boolean hasKey(List<String> lines, String key) {
        return lines.stream().anyMatch(line -> line.stripLeading().startsWith(key + "="));
    }

    private static boolean pgStatus(PostgresBinaries binaries, Path cluster) throws IOException {
        try {
            Process process = new ProcessBuilder(binaries.pgCtl().toString(), "-D", cluster.toString(), "status")
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start();
            return process.waitFor() == 0;
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IOException("Se interrumpio la comprobacion de PostgreSQL.", error);
        }
    }

    private static String psql(PostgresBinaries binaries, Map<String, String> env,
                               String database, String query) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(binaries.psql().toString(), "-X", "-v", "ON_ERROR_STOP=1",
                "-At", "-d", database);
        builder.environment().putAll(env);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        try (OutputStream input = process.getOutputStream()) {
            input.write(query.getBytes(StandardCharsets.UTF_8));
        }
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
        try {
            if (process.waitFor() != 0) {
                throw new IllegalStateException(
                        "No se pudo preparar la base privada de Trullo: " + output);
            }
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IOException("Se interrumpio la preparacion de PostgreSQL.", error);
        }
        return output;
    }

    private static String runCapture(List<String> command, Path directory,
                                     Map<String, String> environment) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command).redirectErrorStream(true);
        if (directory != null) {
            builder.directory(directory.toFile());
        }
        builder.environment().putAll(environment);
        Process process = builder.start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
        try {
            if (process.waitFor() != 0) {
                throw new IllegalStateException("Fallo el comando " + command.getFirst() + ": " + output);
            }
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IOException("Se interrumpio el comando " + command.getFirst(), error);
        }
        return output;
    }

    private static void writePrivate(Path path, String text) throws IOException {
        Files.createDirectories(path.getParent());
        Path temporary = Files.createTempFile(path.getParent(), ".trullo-", ".tmp");
        try {
            Files.writeString(temporary, text, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            setPrivatePermissions(temporary);
            try {
                Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException error) {
                Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static void privateDirectory(Path directory) {
        if (!isWindows()) {
            try {
                Files.setPosixFilePermissions(directory,
                        Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
                                PosixFilePermission.OWNER_EXECUTE));
            } catch (IOException | UnsupportedOperationException ignored) {
                // El sistema de archivos puede no soportar permisos POSIX.
            }
        }
    }

    private static void setPrivatePermissions(Path path) {
        if (!isWindows()) {
            try {
                Files.setPosixFilePermissions(path,
                        Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
            } catch (IOException | UnsupportedOperationException ignored) {
                // El sistema de archivos puede no soportar permisos POSIX.
            }
        }
    }

    private static Path latestSystemPostgres() {
        Path root = Path.of("/usr/lib/postgresql");
        if (!Files.isDirectory(root)) {
            throw new IllegalStateException(
                    "Falta PostgreSQL. Ejecuta el instalador de Linux primero.");
        }
        try (Stream<Path> entries = Files.list(root)) {
            return entries.filter(Files::isDirectory)
                    .map(path -> path.resolve("bin"))
                    .filter(Files::isDirectory)
                    .max(Comparator.comparing(path -> path.getParent().getFileName().toString()))
                    .orElseThrow(() -> new IllegalStateException(
                            "Falta PostgreSQL. Ejecuta el instalador de Linux primero."));
        } catch (IOException error) {
            throw new IllegalStateException("No se pudo inspeccionar /usr/lib/postgresql.", error);
        }
    }

    private static Path findOnPath(String executable) {
        String path = System.getenv("PATH");
        if (path == null) {
            return null;
        }
        String suffix = isWindows() ? ".exe" : "";
        for (String entry : path.split(File.pathSeparator)) {
            Path candidate = Path.of(entry).resolve(executable + suffix);
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private static Path xdgPath(String name, Path fallback) {
        String value = System.getenv(name);
        return value != null && Path.of(value).isAbsolute() ? Path.of(value) : fallback;
    }

    private static Path envPath(String name, Path fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : Path.of(value);
    }

    private static String randomPassword() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
