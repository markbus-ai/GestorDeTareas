package com.trullo.exception;

// usala para entradas inválidas en controllers, no tires IllegalArgument suelta.
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
