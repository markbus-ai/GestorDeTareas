package com.trullo.exception;

public class ProyectoException extends RuntimeException {
    public ProyectoException(String message) {
        super(message);
    }

    public ProyectoException(String message, Throwable cause) {
        super(message, cause);
    }
}
