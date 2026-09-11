package com.trullo.controller;

import java.util.regex.Pattern;

// hijo de mail, implementa al padre
public class EmailNotificationController implements NotificationController {
  private final Pattern pattern = Pattern.compile(
      "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
      Pattern.CASE_INSENSITIVE);

  @Override
  public void validate(String titulo, String contenido, String destino) {
    NotificationController.super.validate(titulo, contenido, destino); // lo común
    if (!pattern.matcher(destino).matches()) {
      throw new IllegalArgumentException("ese email no es válido");
    }
  }

  @Override
  public boolean sendNotification(String titulo, String contenido, String destino) {
    validate(titulo, contenido, destino);
    return true;
  }
}
