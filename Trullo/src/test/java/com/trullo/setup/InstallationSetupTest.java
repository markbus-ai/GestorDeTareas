package com.trullo.setup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class InstallationSetupTest {
    private Path root;

    @Before
    public void createRoot() throws IOException {
        root = Files.createTempDirectory("trullo-setup-test-");
    }

    @After
    public void removeRoot() throws IOException {
        if (root == null || !Files.exists(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            paths.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Los archivos temporales se limpian al cerrar el proceso en el peor caso.
                }
            });
        }
    }

    @Test
    public void stateAndPasswordsArePreserved() throws IOException {
        InstallationSetup.DatabaseState first = InstallationSetup.databaseState(root, 55555);
        InstallationSetup.DatabaseState second = InstallationSetup.databaseState(root, 55556);
        assertEquals(first, second);
        assertEquals(64, first.appPassword().length());
        assertNotEquals(first.adminPassword(), first.appPassword());
        if (!System.getProperty("os.name").startsWith("Windows")) {
            var permissions = Files.getPosixFilePermissions(root.resolve("database-state.json"));
            assertTrue(permissions.contains(java.nio.file.attribute.PosixFilePermission.OWNER_READ));
            assertTrue(permissions.contains(java.nio.file.attribute.PosixFilePermission.OWNER_WRITE));
            assertFalse(permissions.contains(java.nio.file.attribute.PosixFilePermission.GROUP_READ));
            assertFalse(permissions.contains(java.nio.file.attribute.PosixFilePermission.OTHERS_READ));
        }
    }

    @Test
    public void unknownClusterIsNeverReinitialized() throws IOException {
        Files.createDirectories(root.resolve("postgres"));
        Files.writeString(root.resolve("postgres/PG_VERSION"), "17");
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> InstallationSetup.databaseState(root, InstallationSetup.DEFAULT_PORT));
        assertTrue(error.getMessage().contains("sin su configuracion"));
    }

    @Test
    public void configPreservesCredentialsAndAddsPrivateValues() throws IOException {
        Path config = root.resolve("config");
        InstallationSetup.DatabaseState state = InstallationSetup.databaseState(root, InstallationSetup.DEFAULT_PORT);
        Map<String, String> privateValues = privateValues();
        InstallationSetup.writeConfig(config, state, privateValues);
        Path env = config.resolve(".env");
        String original = Files.readString(env);
        assertTrue(original.contains("DB_PASSWORD=\"" + state.appPassword() + "\""));
        assertTrue(original.contains("API_KEY=\"api-key\""));
        assertFalse(original.contains("UV_EXECUTABLE="));

        Files.writeString(env, original
                + "SMTP_FROM=\"cuenta@example.com\"\n"
                + "UV_EXECUTABLE=\"/viejo/uv\"\n"
                + "WHATSPLAY_DIR=\"/viejo/whatsplay\"\n", StandardCharsets.UTF_8);
        InstallationSetup.writeConfig(config, state, privateValues);
        String updated = Files.readString(env);
        assertTrue(updated.contains("DB_PASSWORD=\"" + state.appPassword() + "\""));
        assertTrue(updated.contains("SMTP_FROM=\"cuenta@example.com\""));
        assertEquals(1, updated.lines().filter(line -> line.startsWith("API_KEY=")).count());
        assertFalse(updated.contains("UV_EXECUTABLE="));
        assertFalse(updated.contains("WHATSPLAY_DIR="));
    }

    @Test
    public void secondInstanceIsRejected() throws IOException {
        try (InstallationSetup.SetupLock ignored = InstallationSetup.lock(root)) {
            assertThrows(IllegalStateException.class, () -> InstallationSetup.lock(root));
        }
        try (InstallationSetup.SetupLock ignored = InstallationSetup.lock(root)) {
            assertTrue(Files.exists(root.resolve("setup.lock")));
        }
    }

    @Test
    public void missingPostgresFailsBeforeModifyingDatabase() throws IOException {
        Path empty = Files.createDirectories(root.resolve("empty"));
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> InstallationSetup.postgresBinaries(root, empty));
        assertTrue(error.getMessage().contains("binarios"));
    }

    @Test
    public void privateValuesRejectInjectedValues() throws IOException {
        Path installation = Files.createDirectories(root.resolve("installation"));
        Files.createDirectories(installation.resolve("setup"));
        Files.writeString(installation.resolve("setup/private.env"),
                "WHATSAPP_API_URL=\"http://127.0.0.1/trullo\"\n"
                        + "API_KEY=\"clave\"\n"
                        + "WHATSAPP_API_ZONE=\"America/Argentina/Buenos_Aires\"\n");
        Map<String, String> values = InstallationSetup.privateValues(installation);
        assertEquals("America/Argentina/Buenos_Aires", values.get("WHATSAPP_API_ZONE"));

        Files.writeString(installation.resolve("setup/private.env"),
                "WHATSAPP_API_URL=\"http://127.0.0.1/trullo\"\n"
                        + "API_KEY=\"clave\"inyectada\"\n");
        assertThrows(IllegalStateException.class,
                () -> InstallationSetup.privateValues(installation));
    }

    private static Map<String, String> privateValues() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("WHATSAPP_API_URL", "http://127.0.0.1/trullo");
        values.put("WHATSAPP_API_ZONE", "America/Argentina/Buenos_Aires");
        values.put("API_KEY", "api-key");
        return values;
    }
}
