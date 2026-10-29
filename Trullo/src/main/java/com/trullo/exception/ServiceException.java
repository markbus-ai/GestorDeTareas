package com.trullo.exception;

/**
 * Excepción de la capa de servicios.
 *
 * <p>Envuelve los errores de persistencia ({@link DaoException}) con un mensaje orientado al
 * usuario, de modo que las capas superiores no dependan de los detalles de la base de datos. La
 * causa original se conserva para poder diagnosticar el problema.
 */
public class ServiceException extends RuntimeException {
    /**
     * Crea la excepción con un mensaje y la causa que la originó.
     *
     * @param message el mensaje que describe la operación que falló
     * @param cause la excepción original
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
