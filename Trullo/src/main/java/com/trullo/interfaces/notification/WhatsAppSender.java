package com.trullo.interfaces.notification;

import com.trullo.exception.NotificationException;

/**
 * Envía mensajes de WhatsApp.
 */
public interface WhatsAppSender {
    /**
     * Envía un mensaje.
     *
     * @param to el número de destino, con código de país
     * @param message el texto del mensaje
     * @throws NotificationException si el mensaje no se pudo enviar
     */
    void send(String to, String message);
}
