package com.trullo.exception;

/**
 * Excepción lanzada cuando no se puede enviar una notificación.
 */
public class NotificationException extends RuntimeException {
    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message el motivo del fallo
     */
    public NotificationException(String message) {
        super(message);
    }

    /**
     * Crea la excepción con un mensaje y la causa que la originó.
     *
     * @param message el motivo del fallo
     * @param cause la excepción original
     */
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
