package com.trullo.controller;

public interface NotificationController {
    boolean sendNotification(String titulo, String contenido, String destino);

    default void validate(String titulo, String contenido, String destino) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("titulo vacio, ponele algo");
        }
        if (contenido == null || contenido.isBlank()) {
            throw new IllegalArgumentException("contenido vacio, no mando humo");
        }
        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("destino vacio, a quien le mando?");
        }
    }
}
