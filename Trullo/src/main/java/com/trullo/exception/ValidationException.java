package com.trullo.exception;

/**
 * Excepción lanzada cuando una entrada no cumple las reglas de validación.
 *
 * <p>Se usa para entradas inválidas en los controllers en lugar de lanzar
 * {@link IllegalArgumentException} directamente.
 */
// usala para entradas inválidas en controllers, no tires IllegalArgument suelta.
public class ValidationException extends RuntimeException {
    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message mensaje que describe la validación que falló
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Crea la excepción con un mensaje y la causa que la originó.
     *
     * @param message mensaje que describe la validación que falló
     * @param cause excepción original
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
