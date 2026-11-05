package com.trullo.core;

import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Files;
import java.nio.file.Path;

/** Configuración de desarrollo o de la instalación, independiente del directorio de ejecución. */
public final class AppConfig {
    private AppConfig() { }

    public static Dotenv load() {
        String directory = System.getenv("TRULLO_CONFIG_DIR");
        if (directory == null || directory.isBlank()) {
            directory = Files.isRegularFile(Path.of(".env")) ? "." : configDirectory().toString();
        }
        return Dotenv.configure().directory(directory).ignoreIfMissing().load();
    }

    public static Path configDirectory() {
        String override = System.getenv("TRULLO_CONFIG_DIR");
        if (override != null && !override.isBlank()) return Path.of(override);
        Path home = Path.of(System.getProperty("user.home"));
        if (System.getProperty("os.name").startsWith("Windows")) {
            String appData = System.getenv("APPDATA");
            return (appData == null ? home.resolve("AppData/Roaming") : Path.of(appData)).resolve("Trullo");
        }
        String xdg = System.getenv("XDG_CONFIG_HOME");
        return (xdg != null && Path.of(xdg).isAbsolute() ? Path.of(xdg) : home.resolve(".config"))
                .resolve("trullo");
    }

    public static Path whatsAppScript() {
        String installation = System.getenv("TRULLO_HOME");
        return installation == null ? Path.of("scripts/send_whatsapp.py").toAbsolutePath()
                : Path.of(installation, "setup", "send_whatsapp.py");
    }
}
