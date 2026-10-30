package com.trullo.notification.email;

import com.trullo.interfaces.notification.Notifier;
import com.trullo.interfaces.notification.NotifierFactory;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Optional;

/**
 * Crea el canal de email cuando {@code SMTP_HOST} y {@code SMTP_USER} están configurados.
 *
 * <p>{@code SMTP_PORT} vale 587 si no se indica, y {@code SMTP_FROM} toma el valor de
 * {@code SMTP_USER} si está vacío.
 */
public class EmailNotifierFactory implements NotifierFactory {
    private static final String DEFAULT_STARTTLS_PORT = "587";

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Notifier> create(Dotenv config) {
        String host = read(config, "SMTP_HOST");
        String user = read(config, "SMTP_USER");
        if (host.isEmpty() || user.isEmpty()) {
            return Optional.empty();
        }
        String port = read(config, "SMTP_PORT");
        String from = read(config, "SMTP_FROM");
        SmtpConfig smtp = new SmtpConfig(
                host,
                Integer.parseInt(port.isEmpty() ? DEFAULT_STARTTLS_PORT : port),
                user,
                read(config, "SMTP_PASSWORD"),
                from.isEmpty() ? user : from);
        return Optional.of(new EmailNotifier(smtp));
    }

    private static String read(Dotenv config, String key) {
        return config.get(key, "").trim();
    }
}
