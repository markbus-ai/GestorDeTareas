package com.trullo.interfaces.notification;

import com.trullo.exception.NotificationException;
import com.trullo.exception.ValidationException;
import com.trullo.notification.Message;

/**
 * Canal de envío de notificaciones, como WhatsApp o email.
 *
 * <p>Cada canal se identifica con un id de texto que se guarda en los destinos de los
 * recordatorios. Para sumar un canal nuevo alcanza con implementar esta interfaz y registrar su
 * {@link NotifierFactory}; no hace falta modificar las clases existentes.
 */
public interface Notifier {
    /**
     * Devuelve el identificador del canal, por ejemplo {@code whatsapp}.
     *
     * @return el id del canal, único entre todos los canales registrados
     */
    String channel();

    /**
     * Valida que la dirección tenga el formato que espera el canal.
     *
     * @param address la dirección de destino
     * @throws ValidationException si la dirección no es válida para el canal
     */
    void validate(String address);

    /**
     * Envía un mensaje a una dirección del canal.
     *
     * @param address la dirección de destino
     * @param message el mensaje a enviar
     * @throws NotificationException si el mensaje no se pudo enviar
     */
    void send(String address, Message message);
}
