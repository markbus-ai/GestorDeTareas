package com.trullo.notification.whatsapp;

import com.trullo.exception.NotificationException;
import com.trullo.exception.ValidationException;
import com.trullo.interfaces.notification.Notifier;
import com.trullo.notification.Message;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Canal de WhatsApp que envía mensajes reales con WhatsPlay.
 *
 * <p>Ejecuta {@code scripts/send_whatsapp.py} con el entorno {@code uv} de WhatsPlay. El script
 * abre WhatsApp Web con la sesión ya vinculada, manda el mensaje y termina con código 0 si se
 * envió.
 */
public class WhatsAppNotifier implements Notifier {
    /** Id del canal que se guarda en los destinos de los recordatorios. */
    public static final String CHANNEL = "whatsapp";

    /** Segundos máximos para abrir WhatsApp Web y enviar el mensaje. */
    private static final long TIMEOUT_SECONDS = 180;

    // no tocar el regex, no se como anda pero anda bien
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$");

    private final List<String> launcher;
    private final Path session;

    /**
     * Crea el notificador con el comando que lanza el script.
     *
     * @param launcher el comando que ejecuta el script, sin sus argumentos
     * @param session la carpeta de la sesión vinculada de WhatsApp
     */
    public WhatsAppNotifier(List<String> launcher, Path session) {
        this.launcher = List.copyOf(launcher);
        this.session = session;
    }

    /**
     * Crea el notificador para una instalación de WhatsPlay.
     *
     * @param whatsplayDir la carpeta de WhatsPlay, con su proyecto {@code uv} y su sesión
     * @param script la ruta al script {@code send_whatsapp.py}
     * @return el notificador configurado
     */
    public static WhatsAppNotifier forDirectory(Path whatsplayDir, Path script) {
        List<String> launcher = List.of("uv", "run", "--project", whatsplayDir.toString(), "python",
                script.toAbsolutePath().toString());
        return new WhatsAppNotifier(launcher, whatsplayDir.resolve("whatsapp_session"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String channel() {
        return CHANNEL;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Acepta números con o sin código de país, separados por espacios, guiones o puntos.
     */
    @Override
    public void validate(String address) {
        if (address == null || !PHONE_PATTERN.matcher(address).matches()) {
            throw new ValidationException("número de whatsapp no es válido");
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>El número se normaliza a solo dígitos, que es el formato que espera WhatsPlay, y el
     * mensaje se arma con el título en la primera línea y el texto debajo.
     */
    @Override
    public void send(String address, Message message) {
        List<String> command = new ArrayList<>(launcher);
        command.addAll(List.of("--session", session.toString(), "--to", digitsOnly(address),
                "--message", message.title() + "\n" + message.body()));

        try {
            Process process = new ProcessBuilder(command).inheritIO().start();
            if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new NotificationException("WhatsPlay no terminó en " + TIMEOUT_SECONDS + " segundos");
            }
            if (process.exitValue() != 0) {
                throw new NotificationException("WhatsPlay no pudo enviar el mensaje a " + address);
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
