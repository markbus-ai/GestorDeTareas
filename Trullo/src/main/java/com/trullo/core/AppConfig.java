package com.trullo.core;

import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Files;
import java.nio.file.Path;

/** Carga la configuracion de desarrollo o de la instalacion, sin depender del directorio actual. */
public final class AppConfig {
    private AppConfig() {
    }

    /**
     * Carga el archivo {@code .env} de la instalacion o, en desarrollo, el del directorio actual.
     *
     * @return configuracion combinada con las variables de entorno del proceso
     */
    public static Dotenv load() {
        String directory = configuredDirectory();
        if (directory == null || directory.isBlank()) {
            directory = Files.isRegularFile(Path.of(".env")) ? "." : configDirectory().toString();
        }
        return Dotenv.configure().directory(directory).ignoreIfMissing().load();
    }

    /**
     * Carpeta de configuracion por usuario.
     *
     * @return {@code %APPDATA%\Trullo} en Windows o {@code $XDG_CONFIG_HOME/trullo} en Linux
     */
    public static Path configDirectory() {
        String override = configuredDirectory();
        if (override != null && !override.isBlank()) {
            return Path.of(override);
        }
        Path home = Path.of(System.getProperty("user.home"));
        if (System.getProperty("os.name").startsWith("Windows")) {
            String appData = System.getenv("APPDATA");
            return (appData == null ? home.resolve("AppData/Roaming") : Path.of(appData)).resolve("Trullo");
        }
        String xdg = System.getenv("XDG_CONFIG_HOME");
        return (xdg != null && Path.of(xdg).isAbsolute() ? Path.of(xdg) : home.resolve(".config"))
                .resolve("trullo");
    }

    private static String configuredDirectory() {
        String property = System.getProperty("trullo.config.dir");
        return property != null && !property.isBlank() ? property : System.getenv("TRULLO_CONFIG_DIR");
    }
}
