package com.trullo.controller;

import com.trullo.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Controlador de notificaciones por correo electrónico.
 *
 * <p>Implementa el canal de email de {@link NotificationController}: reutiliza la validación
 * común de la interfaz y agrega la comprobación del formato del destino mediante una
 * expresión regular. Si el destino no tiene formato de correo, la validación falla.
 */
// hijo de mail, implementa al padre
public class EmailNotificationController implements NotificationController {
  /**
   * Expresión regular compilada que se usa para validar el formato de la dirección de correo.
   */
  private final Pattern pattern = Pattern.compile(
      "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
      Pattern.CASE_INSENSITIVE);

  /**
   * Valida los datos de la notificación, incluido el formato del correo de destino.
   *
   * <p>Aplica primero la validación común de {@link NotificationController} y luego exige que
   * {@code destino} coincida con el patrón de correo electrónico.
   *
   * @param titulo el título de la notificación
   * @param contenido el contenido de la notificación
   * @param destino la dirección de correo de destino
   * @throws ValidationException si algún campo es nulo o vacío, o si {@code destino} no es un correo válido
   */
  @Override
  public void validate(String titulo, String contenido, String destino) {
    NotificationController.super.validate(titulo, contenido, destino); // lo común
    if (!pattern.matcher(destino).matches()) {
      throw new ValidationException("email no es válido");
    }
  }

  /**
   * Envía una notificación por correo electrónico tras validar los datos.
   *
   * <p>Esta implementación no realiza un envío real: valida los datos y retorna {@code true}
   * cuando la validación es correcta.
   *
   * @param titulo el título de la notificación
   * @param contenido el contenido de la notificación
   * @param destino la dirección de correo de destino
   * @return {@code true} cuando la notificación se procesa correctamente
   * @throws ValidationException si algún campo es nulo o vacío, o si {@code destino} no es un correo válido
   */
  @Override
  public boolean sendNotification(String titulo, String contenido, String destino) {
    validate(titulo, contenido, destino);
    return true;
  }
}
