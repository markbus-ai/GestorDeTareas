package com.trullo.controller;

import com.trullo.exception.ValidationException;
import com.trullo.interfaces.services.ReminderTargetService;
import com.trullo.model.ReminderTarget;
import com.trullo.notification.NotifierRegistry;
import java.util.List;

/**
 * Controlador de destinos de recordatorios.
 *
 * <p>Valida las entradas de los destinos y delega en {@link ReminderTargetService}. Cada
 * dirección se valida con el canal correspondiente del {@link NotifierRegistry}.
 */
public class ReminderTargetController implements CrudValidator {
    private final ReminderTargetService targetService;
    private final NotifierRegistry notifiers;

    /**
     * Crea un controlador de destinos de recordatorios.
     *
     * @param targetService el servicio de destinos a utilizar; no puede ser {@code null}
     * @param notifiers los canales de notificación disponibles; no puede ser {@code null}
     * @throws ValidationException si algún parámetro es {@code null}
     */
    public ReminderTargetController(ReminderTargetService targetService, NotifierRegistry notifiers) {
        requireNonNull(targetService, "targetService");
        requireNonNull(notifiers, "notifiers");
        this.targetService = targetService;
        this.notifiers = notifiers;
    }

    /**
     * Agrega un destino de envío a un recordatorio.
     *
     * @param reminderId el identificador del recordatorio
     * @param channel el identificador del canal, por ejemplo {@code whatsapp} o {@code email}
     * @param address la dirección de destino dentro del canal
     * @return el destino creado devuelto por el servicio
     * @throws ValidationException si falta algún dato, si el canal no está disponible o si la
     *                             dirección no es válida para el canal
     */
    public ReminderTarget agregarDestino(Long reminderId, String channel, String address) {
        validateId(reminderId);
        requireText(channel, "channel");
        requireText(address, "address");
        notifiers.validate(channel, address);
        return targetService.agregarDestino(reminderId, channel, address);
    }

    /**
     * Lista los destinos de un recordatorio.
     *
     * @param reminderId el identificador del recordatorio
     * @return los destinos del recordatorio
     * @throws ValidationException si {@code reminderId} es {@code null}
     */
    public List<ReminderTarget> listarDestinos(Long reminderId) {
        validateId(reminderId);
        return targetService.listarDestinos(reminderId);
    }
}
