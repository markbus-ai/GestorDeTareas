package com.trullo.model;

/**
 * Canales disponibles para enviar recordatorios.
 *
 * @author Nahuel Romero
 */
public enum NotificationChannel {
    /** Envío por WhatsApp. */
    WHATSAPP,
    /** Envío por correo electrónico. */
    EMAIL,
    /** Envío por ambos canales. */
    AMBOS
}
