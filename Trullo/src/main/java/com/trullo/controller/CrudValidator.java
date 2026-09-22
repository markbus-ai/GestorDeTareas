package com.trullo.controller;

import com.trullo.exception.ValidationException;

//implementar esto en los controllers y no repitir las validaciones en cada uno.
public interface CrudValidator {
    // si el id viene nulo rebotá acá de una.
    default void validateId(Long id) {
        if (id == null) {
            throw new ValidationException("id no puede ser null");
        }
    }

    // usar para los textos que no pueden venir nulos ni vacíos.
    default void requireText(String value, String campo) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(campo + " no puede ser null ni vacio");
        }
    }

    // si pasan null corta acá nomás.
    default void requireNonNull(Object value, String campo) {
        if (value == null) {
            throw new ValidationException(campo + " no puede ser null");
        }
    }

    // para los números que tienen que venir en positivo, días por ejemplo.
    default void requirePositive(Integer value, String campo) {
        requireNonNull(value, campo);
        if (value <= 0) {
            throw new ValidationException(campo + " debe ser mayor a cero");
        }
    }
}
