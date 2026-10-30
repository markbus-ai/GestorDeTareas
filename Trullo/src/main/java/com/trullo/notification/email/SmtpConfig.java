package com.trullo.notification.email;

/**
 * Datos de conexión al servidor SMTP que usa el canal de email.
 *
 * @param host el servidor SMTP, por ejemplo {@code smtp.gmail.com}
 * @param port el puerto SMTP con STARTTLS, normalmente 587
 * @param user el usuario con el que se autentica
 * @param password la contraseña del usuario; en Gmail, una contraseña de aplicación
 * @param from la dirección que figura como remitente
 */
public record SmtpConfig(String host, int port, String user, String password, String from) {
}
