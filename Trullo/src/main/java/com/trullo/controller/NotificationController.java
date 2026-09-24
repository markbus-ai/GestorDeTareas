package com.trullo.controller;

/**
 * Contrato de la capa de controladores para el envío de notificaciones.
 *
 * <p>Define la operación de envío y una validación común de los datos (título, contenido y
 * destino) que los canales concretos pueden reutilizar y extender. Hereda las validaciones
 * básicas de {@link CrudValidator}.
 */
public interface NotificationController extends CrudValidator {
    /**
     * Envía una notificación por el canal correspondiente.
     *
     * <p>Las implementaciones deben validar los datos antes de procesar el envío.
     *
     * @param titulo el título de la notificación
     * @param contenido el contenido de la notificación
     * @param destino el destino de la notificación, cuyo formato depende del canal
     * @return {@code true} cuando la notificación se procesa correctamente
     * @throws ValidationException si los datos de la notificación no son válidos
     */
    boolean sendNotification(String titulo, String contenido, String destino);

    /**
     * Valida los datos comunes de una notificación.
     *
     * <p>Exige que título, contenido y destino no sean nulos ni estén en blanco.
     *
     * @param titulo el título de la notificación
     * @param contenido el contenido de la notificación
     * @param destino el destino de la notificación
     * @throws ValidationException si alguno de los campos es nulo o está en blanco
     */
    default void validate(String titulo, String contenido, String destino) {
        requireText(titulo, "titulo");
        requireText(contenido, "contenido");
        requireText(destino, "destino");
    }
}
