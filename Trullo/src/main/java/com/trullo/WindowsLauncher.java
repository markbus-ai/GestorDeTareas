package com.trullo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Entrada del ejecutable Windows: prepara los servicios antes de abrir la interfaz. */
public final class WindowsLauncher {
    private WindowsLauncher() { }

    public static void main(String[] args) throws Exception {
        Path installation = Path.of(System.getProperty("jpackage.app-path")).toAbsolutePath().getParent();
        Path powershell = Path.of(System.getenv("SystemRoot"), "System32", "WindowsPowerShell",
                "v1.0", "powershell.exe");
        List<String> command = new ArrayList<>(List.of(powershell.toString(), "-NoProfile",
                "-ExecutionPolicy", "Bypass", "-File", installation.resolve("setup/launch.ps1").toString()));
        command.addAll(List.of(args));
        int exitCode = new ProcessBuilder(command).inheritIO().start().waitFor();
        System.exit(exitCode);
    }
}
