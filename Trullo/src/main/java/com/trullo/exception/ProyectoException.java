package com.trullo.exception;

/**
 * Excepción relacionada con las operaciones sobre proyectos.
 *
 * <p>Se utiliza para señalar errores propios de la lógica de proyectos que no encajan
 * en las excepciones de servicio o acceso a datos.
 */
public class ProyectoException extends RuntimeException {
    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message mensaje que describe el error
     */
    public ProyectoException(String message) {
        super(message);
    }

    /**
     * Crea la excepción con un mensaje y la causa que la originó.
     *
     * @param message mensaje que describe el error
     * @param cause excepción original
     */
    public ProyectoException(String message, Throwable cause) {
        super(message, cause);
    }
}
