package com.trullo.controller;

import com.trullo.exception.ValidationException;

/**
 * Define validaciones reutilizables para la capa de controladores.
 *
 * <p>Agrupa comprobaciones básicas de entradas (identificadores, textos, referencias y
 * valores numéricos positivos) con el fin de evitar repetirlas en cada controlador. Los
 * métodos son {@code default} y lanzan {@link com.trullo.exception.ValidationException}
 * cuando la entrada no cumple la regla indicada.
 */
//implementar esto en los controllers y no repitir las validaciones en cada uno.
public interface CrudValidator {
    /**
     * Verifica que el identificador recibido no sea {@code null}.
     *
     * @param id el identificador a validar
     * @throws ValidationException si {@code id} es {@code null}
     */
    // si el id viene nulo rebotá acá de una.
    default void validateId(Long id) {
        if (id == null) {
            throw new ValidationException("id no puede ser null");
        }
    }

    /**
     * Verifica que un texto no sea {@code null} ni esté en blanco.
     *
     * @param value el texto a validar
     * @param campo nombre del campo que se incluye en el mensaje de error
     * @throws ValidationException si {@code value} es {@code null} o está en blanco
     */
    // usar para los textos que no pueden venir nulos ni vacíos.
    default void requireText(String value, String campo) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(campo + " no puede ser null ni vacio");
        }
    }

    /**
     * Verifica que la referencia recibida no sea {@code null}.
     *
     * @param value la referencia a validar
     * @param campo nombre del campo que se incluye en el mensaje de error
     * @throws ValidationException si {@code value} es {@code null}
     */
    // si pasan null corta acá nomás.
    default void requireNonNull(Object value, String campo) {
        if (value == null) {
            throw new ValidationException(campo + " no puede ser null");
        }
    }

    /**
     * Verifica que un número entero sea mayor que cero.
     *
     * <p>Primero comprueba que el valor no sea {@code null} y luego que sea positivo.
     *
     * @param value el número a validar
     * @param campo nombre del campo que se incluye en el mensaje de error
     * @throws ValidationException si {@code value} es {@code null} o no es mayor que cero
     */
    // para los números que tienen que venir en positivo, días por ejemplo.
    default void requirePositive(Integer value, String campo) {
        requireNonNull(value, campo);
        if (value <= 0) {
            throw new ValidationException(campo + " debe ser mayor a cero");
        }
    }
}
