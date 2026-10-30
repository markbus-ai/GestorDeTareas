package com.trullo.controller;

import com.trullo.exception.NotificationException;
import com.trullo.exception.ValidationException;
import com.trullo.interfaces.notification.WhatsAppSender;
import java.util.regex.Pattern;

/**
 * Controlador de notificaciones por WhatsApp.
 *
 * <p>Implementa el canal de WhatsApp de {@link NotificationController}: reutiliza la validación
 * común de la interfaz y agrega la comprobación del formato del número de destino mediante una
 * expresión regular. Si el número es válido, delega el envío en un {@link WhatsAppSender}.
 */
// hijo de wsp, implementa al padre
public class WspNotificationController implements NotificationController {
  /**
   * Expresión regular compilada que se usa para validar el formato del número de teléfono.
   */
  // no tocar el regex, no se como anda pero anda bien
  private final Pattern pattern = Pattern.compile(
      "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$");

  private final WhatsAppSender sender;

  /**
   * Crea el controlador de notificaciones por WhatsApp.
   *
   * @param sender el medio de envío de WhatsApp; no puede ser {@code null}
   * @throws ValidationException si {@code sender} es {@code null}
   */
  public WspNotificationController(WhatsAppSender sender) {
    requireNonNull(sender, "sender");
    this.sender = sender;
  }

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
   * Valida los datos y envía la notificación por WhatsApp.
   *
   * <p>El mensaje se arma con el título en la primera línea y el contenido debajo.
   *
   * @param titulo el título de la notificación
   * @param contenido el contenido de la notificación
   * @param destino el número de teléfono de destino
   * @return {@code true} cuando el mensaje se envió
   * @throws ValidationException si algún campo es nulo o vacío, o si {@code destino} no es un número válido
   * @throws NotificationException si el mensaje no se pudo enviar
   */
  @Override
  public boolean sendNotification(String titulo, String contenido, String destino) {
    validate(titulo, contenido, destino);
    sender.send(destino, titulo + "\n" + contenido);
    return true;
  }
}
