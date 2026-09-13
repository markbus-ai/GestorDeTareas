package com.trullo.controller;

import com.trullo.exception.ValidationException;

public interface NotificationController {
    boolean sendNotification(String titulo, String contenido, String destino);

    default void validate(String titulo, String contenido, String destino) {
        if (titulo == null || titulo.isBlank()) {
            throw new ValidationException("titulo no puede ser nulo ni vacio");
        }
        if (contenido == null || contenido.isBlank()) {
            throw new ValidationException("contenido no puede ser nulo ni vacio");
        }
        if (destino == null || destino.isBlank()) {
            throw new ValidationException("destino no puede ser nulo ni vacio");
        }
    }
}
