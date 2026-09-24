package com.trullo.controller;

import com.trullo.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Controlador de notificaciones por WhatsApp.
 *
 * <p>Implementa el canal de WhatsApp de {@link NotificationController}: reutiliza la validación
 * común de la interfaz y agrega la comprobación del formato del número de destino mediante una
 * expresión regular. Si el número no tiene un formato válido, la validación falla.
 */
// hijo de wsp, implementa al padre
public class WspNotificationController implements NotificationController {
  /**
   * Expresión regular compilada que se usa para validar el formato del número de teléfono.
   */
  // no tocar el regex, no se como anda pero anda bien
  private final Pattern pattern = Pattern.compile(
      "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$");

  /**
   * Valida los datos de la notificación, incluido el formato del número de WhatsApp.
   *
   * <p>Aplica primero la validación común de {@link NotificationController} y luego exige que
   * {@code destino} coincida con el patrón de número de teléfono.
   *
   * @param titulo el título de la notificación
   * @param contenido el contenido de la notificación
   * @param destino el número de teléfono de destino
   * @throws ValidationException si algún campo es nulo o vacío, o si {@code destino} no es un número válido
   */
  @Override
  public void validate(String titulo, String contenido, String destino) {
    NotificationController.super.validate(titulo, contenido, destino); // lo común
    if (!pattern.matcher(destino).matches()) {
      throw new ValidationException("número de whatsapp no es válido");
    }
  }

  /**
   * Envía una notificación por WhatsApp tras validar los datos.
   *
   * <p>Esta implementación no realiza un envío real: valida los datos y retorna {@code true}
   * cuando la validación es correcta.
   *
   * @param titulo el título de la notificación
   * @param contenido el contenido de la notificación
   * @param destino el número de teléfono de destino
   * @return {@code true} cuando la notificación se procesa correctamente
   * @throws ValidationException si algún campo es nulo o vacío, o si {@code destino} no es un número válido
   */
  @Override
  public boolean sendNotification(String titulo, String contenido, String destino) {
    validate(titulo, contenido, destino);
    return true;
  }
}
