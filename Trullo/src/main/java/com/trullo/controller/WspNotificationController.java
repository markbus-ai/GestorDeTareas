package com.trullo.controller;

import java.util.regex.Pattern;

// hijo de wsp, implementa al padre
public class WspNotificationController implements NotificationController {
  // no tocar el regex, no se como anda pero anda bien
  private final Pattern pattern = Pattern.compile(
      "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$");

  @Override
  public void validate(String titulo, String contenido, String destino) {
    NotificationController.super.validate(titulo, contenido, destino); // lo común
    if (!pattern.matcher(destino).matches()) {
      throw new IllegalArgumentException("ese número de wsp no tiene cara de número");
    }
  }

  @Override
  public boolean sendNotification(String titulo, String contenido, String destino) {
    validate(titulo, contenido, destino);
    return true;
  }
}
