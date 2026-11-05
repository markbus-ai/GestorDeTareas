package com.trullo.setup;

import org.junit.Assume;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class PostgresSetupTest {
    @Test
    public void newInstallationUpgradeAndUnknownSchema() throws Exception {
        Assume.assumeTrue("Opt-in: cluster temporal aislado",
                "1".equals(System.getenv("TRULLO_SETUP_TEST_POSTGRES")));
        Path root = Files.createTempDirectory("trullo-pg-test-");
        Path data = Files.createDirectories(root.resolve("data"));
        Path installation = root.resolve("application");
        copySql(installation);
        InstallationSetup.PostgresBinaries binaries = InstallationSetup.postgresBinaries(installation, null);
        int port;
        try (ServerSocket socket = new ServerSocket(0, 0, InetAddress.getLoopbackAddress())) {
            port = socket.getLocalPort();
        }
        try {
            InstallationSetup.DatabaseState first =
                    InstallationSetup.prepareDatabase(installation, data, binaries, port);
            assertEquals("0", sql(binaries, port, "trullo_app", first.appPassword(),
                    "trullo", "SELECT count(*) FROM tasks"));
            sql(binaries, port, "trullo_app", first.appPassword(),
                    "trullo", "INSERT INTO notes(title,content) VALUES ('conservar','actualizacion')");
            InstallationSetup.DatabaseState second =
                    InstallationSetup.prepareDatabase(installation, data, binaries, port);
            assertEquals(first, second);
            assertEquals("conservar", sql(binaries, port, "trullo_app", first.appPassword(),
                    "trullo", "SELECT title FROM notes"));
            sql(binaries, port, "trullo_admin", first.adminPassword(),
                    "trullo", "DROP TABLE trullo_schema_version");
            assertThrows(IllegalStateException.class,
                    () -> InstallationSetup.prepareDatabase(installation, data, binaries, port));
            assertEquals("conservar", sql(binaries, port, "trullo_app", first.appPassword(),
                    "trullo", "SELECT title FROM notes"));
        } finally {
            InstallationSetup.stopPostgres(data, binaries);
            deleteRecursively(root);
        }
    }

    private static void copySql(Path installation) throws IOException {
        Path source = Path.of("src/main/resources/sql");
        Path target = Files.createDirectories(installation.resolve("setup/sql"));
        for (String name : new String[]{"enums.sql", "schema.sql"}) {
            Files.copy(source.resolve(name), target.resolve(name));
        }
    }

    private static String sql(InstallationSetup.PostgresBinaries binaries, int port,
                              String user, String password, String database, String query)
            throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(binaries.psql().toString(), "-X", "-v", "ON_ERROR_STOP=1",
                "-At", "-d", database);
        builder.environment().putAll(Map.of(
                "PGHOST", "127.0.0.1",
                "PGPORT", String.valueOf(port),
                "PGUSER", user,
                "PGPASSWORD", password,
                "PGCONNECT_TIMEOUT", "5",
                "PGCLIENTENCODING", "UTF8"));
        builder.redirectErrorStream(true);
        Process process = builder.start();
        try (OutputStream input = process.getOutputStream()) {
            input.write(query.getBytes(StandardCharsets.UTF_8));
        }
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
        assertEquals(output, 0, process.waitFor());
        return output;
    }

    private static void deleteRecursively(Path root) throws IOException {
        try (var paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // La limpieza no debe ocultar el resultado de la prueba.
                }
            });
        }
    }
}
