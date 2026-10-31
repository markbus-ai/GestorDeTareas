package com.trullo.notification;

/**
 * Mensaje que se envía por cualquier canal de notificación.
 *
 * <p>Cada canal decide cómo presentarlo; por ejemplo, el email usa el título como asunto.
 *
 * @param title el título del mensaje
 * @param body el texto del mensaje
 */
public record Message(String title, String body) {
}
