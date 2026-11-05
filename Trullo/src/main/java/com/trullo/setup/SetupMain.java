package com.trullo.setup;

import com.trullo.TrulloApp;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JOptionPane;

/** Prepara la instalacion y arranca Trullo con su base PostgreSQL privada. */
public final class SetupMain {
    private SetupMain() {
    }

    /** Punto de entrada del app-image generado por jpackage. */
    public static void main(String[] args) {
        Arguments arguments;
        try {
            arguments = Arguments.parse(args);
        } catch (IllegalArgumentException error) {
            System.err.println(error.getMessage());
            printHelp();
            System.exit(2);
            return;
        }
        Path installation = arguments.home() != null
                ? arguments.home().toAbsolutePath().normalize()
                : InstallationSetup.installationDirectory();
        InstallationSetup.UserPaths defaults = InstallationSetup.userPaths();
        Path config = arguments.configDir() != null
                ? arguments.configDir().toAbsolutePath().normalize() : defaults.config();
        Path data = arguments.dataDir() != null
                ? arguments.dataDir().toAbsolutePath().normalize() : defaults.data();
        System.setProperty("trullo.config.dir", config.toString());
        try {
            Files.createDirectories(data);
            if (arguments.setupOnly()) {
                prepareOnly(installation, config, data, arguments);
                return;
            }
            run(installation, config, data, arguments);
        } catch (Throwable error) {
            report(error, data);
            System.exit(1);
        }
    }

    private static void prepareOnly(Path installation, Path config, Path data, Arguments arguments) {
        try (InstallationSetup.SetupLock ignored = InstallationSetup.lock(data)) {
            InstallationSetup.PostgresBinaries binaries =
                    InstallationSetup.postgresBinaries(installation, arguments.pgBin());
            try {
                InstallationSetup.DatabaseState state = InstallationSetup.prepareDatabase(
                        installation, data, binaries, arguments.port());
                InstallationSetup.writeConfig(config, state,
                        InstallationSetup.privateValues(installation));
                System.out.println("Trullo esta preparado. La configuracion existente se conserva.");
            } finally {
                InstallationSetup.stopPostgres(data, binaries);
            }
        } catch (IOException error) {
            throw new IllegalStateException("No se pudo preparar Trullo: " + error.getMessage(), error);
        }
    }

    private static void run(Path installation, Path config, Path data, Arguments arguments) {
        InstallationSetup.PostgresBinaries binaries =
                InstallationSetup.postgresBinaries(installation, arguments.pgBin());
        InstallationSetup.SetupLock lock;
        try {
            lock = InstallationSetup.lock(data);
        } catch (IOException error) {
            throw new IllegalStateException("No se pudo bloquear la instalacion: " + error.getMessage(), error);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            InstallationSetup.stopPostgres(data, binaries);
            try {
                lock.close();
            } catch (IOException ignored) {
                // La JVM ya esta terminando.
            }
        }, "trullo-shutdown"));
        try {
            InstallationSetup.DatabaseState state = InstallationSetup.prepareDatabase(
                    installation, data, binaries, arguments.port());
            InstallationSetup.writeConfig(config, state, InstallationSetup.privateValues(installation));
            TrulloApp.main(new String[0]);
        } catch (IOException error) {
            throw new IllegalStateException("No se pudo preparar Trullo: " + error.getMessage(), error);
        }
    }

    private static void report(Throwable error, Path data) {
        StringWriter trace = new StringWriter();
        error.printStackTrace(new PrintWriter(trace));
        String message = error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
        Path log = data.resolve("setup-error.log");
        try {
            Files.createDirectories(data);
            Files.writeString(log, trace.toString(), StandardCharsets.UTF_8);
            message += "\n\nDetalle en " + log;
        } catch (IOException ignored) {
            // Se muestra el error original aunque no se pueda escribir el log.
        }
        System.err.println(message);
        if (!GraphicsEnvironment.isHeadless()) {
            JOptionPane.showMessageDialog(null, message, "No se pudo iniciar Trullo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void printHelp() {
        System.out.println("""
                Uso: Trullo [opciones]
                  --home DIR        Carpeta de la aplicacion (se detecta sola en el app-image)
                  --config-dir DIR  Carpeta de configuracion (por defecto, la del usuario)
                  --data-dir DIR    Carpeta de datos (por defecto, la del usuario)
                  --pg-bin DIR      Carpeta de binarios PostgreSQL (por defecto, la del sistema)
                  --port NUM        Puerto privado de PostgreSQL (por defecto, 54329)
                  --setup-only      Prepara la base y la configuracion sin abrir la interfaz
                  --repair          Vuelve a comprobar y reparar la preparacion
                  --help            Muestra esta ayuda
                """);
    }

    private record Arguments(Path home, Path configDir, Path dataDir, Path pgBin,
                             int port, boolean setupOnly, boolean repair) {
        private static Arguments parse(String[] args) {
            Path home = null;
            Path configDir = null;
            Path dataDir = null;
            Path pgBin = null;
            int port = InstallationSetup.DEFAULT_PORT;
            boolean setupOnly = false;
            boolean repair = false;
            for (int index = 0; index < args.length; index++) {
                String argument = args[index];
                switch (argument) {
                    case "--home" -> home = Path.of(next(args, ++index, argument));
                    case "--config-dir" -> configDir = Path.of(next(args, ++index, argument));
                    case "--data-dir" -> dataDir = Path.of(next(args, ++index, argument));
                    case "--pg-bin" -> pgBin = Path.of(next(args, ++index, argument));
                    case "--port" -> port = parsePort(next(args, ++index, argument));
                    case "--setup-only" -> setupOnly = true;
                    case "--repair" -> repair = true;
                    case "--help", "-h" -> {
                        printHelp();
                        System.exit(0);
                    }
                    default -> throw new IllegalArgumentException("Argumento desconocido: " + argument);
                }
            }
            return new Arguments(home, configDir, dataDir, pgBin, port, setupOnly, repair);
        }

        private static String next(String[] args, int index, String option) {
            if (index >= args.length) {
                throw new IllegalArgumentException("Falta el valor de " + option);
            }
            return args[index];
        }

        private static int parsePort(String value) {
            try {
                int port = Integer.parseInt(value);
                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                }
                return port;
            } catch (NumberFormatException error) {
                throw new IllegalArgumentException("Puerto invalido: " + value);
            }
        }
    }
}
