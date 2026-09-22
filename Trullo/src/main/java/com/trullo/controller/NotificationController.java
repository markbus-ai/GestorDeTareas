package com.trullo.controller;

public interface NotificationController extends CrudValidator {
    boolean sendNotification(String titulo, String contenido, String destino);

    default void validate(String titulo, String contenido, String destino) {
        requireText(titulo, "titulo");
        requireText(contenido, "contenido");
        requireText(destino, "destino");
    }
}
