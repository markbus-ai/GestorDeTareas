package com.trullo.notification;

import com.trullo.exception.NotificationException;
import com.trullo.interfaces.notification.WhatsAppSender;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Envía mensajes reales con WhatsPlay.
 *
 * <p>Ejecuta {@code scripts/send_whatsapp.py} con el entorno {@code uv} de WhatsPlay. El script
 * abre WhatsApp Web con la sesión ya vinculada, manda el mensaje y termina con código 0 si se
 * envió.
 */
public class WhatsPlaySender implements WhatsAppSender {
    /** Segundos máximos para abrir WhatsApp Web y enviar el mensaje. */
    private static final long TIMEOUT_SECONDS = 180;

    private final List<String> launcher;
    private final Path session;

    /**
     * Crea el sender con el comando que lanza el script.
     *
     * @param launcher el comando que ejecuta el script, sin sus argumentos
     * @param session la carpeta de la sesión vinculada de WhatsApp
     */
    public WhatsPlaySender(List<String> launcher, Path session) {
        this.launcher = List.copyOf(launcher);
        this.session = session;
    }

    /**
     * Crea el sender para una instalación de WhatsPlay.
     *
     * @param whatsplayDir la carpeta de WhatsPlay, con su proyecto {@code uv} y su sesión
     * @param script la ruta al script {@code send_whatsapp.py}
     * @return el sender configurado
     */
    public static WhatsPlaySender forDirectory(Path whatsplayDir, Path script) {
        List<String> launcher = List.of("uv", "run", "--project", whatsplayDir.toString(), "python",
                script.toAbsolutePath().toString());
        return new WhatsPlaySender(launcher, whatsplayDir.resolve("whatsapp_session"));
    }

    /**
     * {@inheritDoc}
     *
     * <p>El número se normaliza a solo dígitos, que es el formato que espera WhatsPlay.
     */
    @Override
    public void send(String to, String message) {
        List<String> command = new ArrayList<>(launcher);
        command.addAll(List.of("--session", session.toString(), "--to", digitsOnly(to), "--message", message));

        try {
            Process process = new ProcessBuilder(command).inheritIO().start();
            if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new NotificationException("WhatsPlay no terminó en " + TIMEOUT_SECONDS + " segundos");
            }
            if (process.exitValue() != 0) {
                throw new NotificationException("WhatsPlay no pudo enviar el mensaje a " + to);
            }
        } catch (IOException e) {
            throw new NotificationException("No se pudo ejecutar WhatsPlay", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NotificationException("El envío por WhatsApp se interrumpió", e);
        }
    }

    private static String digitsOnly(String phone) {
        return phone.replaceAll("\\D", "");
    }
}
