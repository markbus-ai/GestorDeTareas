package com.trullo.notification.email;

import com.trullo.exception.NotificationException;
import com.trullo.exception.ValidationException;
import com.trullo.interfaces.notification.Notifier;
import com.trullo.notification.Message;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Canal de email que envía mensajes por SMTP con STARTTLS y autenticación.
 *
 * <p>El título del mensaje se usa como asunto y el texto como cuerpo en texto plano.
 */
public class EmailNotifier implements Notifier {
    /** Id del canal que se guarda en los destinos de los recordatorios. */
    public static final String CHANNEL = "email";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

    private final SmtpConfig config;
    private final Session session;

    /**
     * Crea el notificador con los datos del servidor SMTP.
     *
     * @param config los datos de conexión al servidor SMTP
     */
    public EmailNotifier(SmtpConfig config) {
        this.config = config;
        this.session = Session.getInstance(smtpProperties(config), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.user(), config.password());
            }
        });
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
     */
    @Override
    public void validate(String address) {
        if (address == null || !EMAIL_PATTERN.matcher(address).matches()) {
            throw new ValidationException("email no es válido");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(String address, Message message) {
        try {
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(config.from()));
            email.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(address));
            email.setSubject(message.title(), StandardCharsets.UTF_8.name());
            email.setText(message.body(), StandardCharsets.UTF_8.name());
            Transport.send(email);
        } catch (MessagingException e) {
            throw new NotificationException("No se pudo enviar el email a " + address, e);
        }
    }

    private static Properties smtpProperties(SmtpConfig config) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", config.host());
        properties.put("mail.smtp.port", String.valueOf(config.port()));
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        return properties;
    }
}
