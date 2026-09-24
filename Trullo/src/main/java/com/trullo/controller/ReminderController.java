package com.trullo.controller;

import com.trullo.exception.ValidationException;
import com.trullo.model.NotificationChannel;
import com.trullo.model.Reminder;
import com.trullo.service.ReminderService;
import java.util.List;

/**
 * Controlador de recordatorios de la aplicación.
 *
 * <p>Valida las entradas correspondientes a recordatorios y delega las operaciones de alta,
 * baja, modificación y consulta en {@link ReminderService}. Un recordatorio requiere
 * {@code taskId}, {@code scheduledAt} y {@code channel} no nulos, y el destino debe
 * corresponder al canal indicado: teléfono para WhatsApp, correo para email, y ambos para el
 * canal combinado.
 */
public class ReminderController implements CrudValidator {
    private final ReminderService reminderService;

    /**
     * Crea un controlador de recordatorios.
     *
     * @param reminderService el servicio de recordatorios a utilizar; no puede ser {@code null}
     * @throws ValidationException si {@code reminderService} es {@code null}
     */
    public ReminderController(ReminderService reminderService) {
        requireNonNull(reminderService, "reminderService");
        this.reminderService = reminderService;
    }

    /**
     * Crea un recordatorio nuevo.
     *
     * @param reminder el recordatorio a crear
     * @return el recordatorio creado devuelto por el servicio
     * @throws ValidationException si el recordatorio es nulo, si faltan datos obligatorios, o
     *                             si el destino no es válido para el canal indicado
     */
    public Reminder crear(Reminder reminder) {
        validateReminder(reminder, false);
        return reminderService.crear(reminder);
    }

    /**
     * Modifica un recordatorio existente.
     *
     * @param reminder el recordatorio con los datos actualizados
     * @return el recordatorio modificado devuelto por el servicio
     * @throws ValidationException si el recordatorio es nulo, si su identificador es nulo, si
     *                             faltan datos obligatorios, o si el destino no es válido para
     *                             el canal indicado
     */
    public Reminder modificar(Reminder reminder) {
        validateReminder(reminder, true);
        return reminderService.modificar(reminder);
    }

    /**
     * Elimina un recordatorio por su identificador.
     *
     * @param id el identificador del recordatorio a eliminar
     * @throws ValidationException si {@code id} es {@code null}
     */
    public void eliminar(Long id) {
        validateId(id);
        reminderService.eliminar(id);
    }

    /**
     * Obtiene un recordatorio por su identificador.
     *
     * @param id el identificador del recordatorio
     * @return el recordatorio encontrado
     * @throws ValidationException si {@code id} es {@code null}
     */
    public Reminder obtenerPorId(Long id) {
        validateId(id);
        return reminderService.obtenerPorId(id);
    }

    /**
     * Lista todos los recordatorios.
     *
     * @return la lista de recordatorios devuelta por el servicio
     */
    public List<Reminder> listar() {
        return reminderService.listar();
    }

    /**
     * Valida un recordatorio antes de enviarlo al servicio.
     *
     * @param reminder el recordatorio a validar
     * @param requireId indica si debe exigirse un identificador no nulo
     * @throws ValidationException si el recordatorio es nulo, si se exige identificador y este
     *                             es nulo, si faltan datos obligatorios, o si el destino no es
     *                             válido para el canal indicado
     */
    // validar el reminder antes de mandarlo al service, no dejes pasar canales truchos.
    // chequeá el destino según el canal, no aceptes teléfonos ni mails vacíos.
    private void validateReminder(Reminder reminder, boolean requireId) {
        requireNonNull(reminder, "reminder");
        if (requireId) {
            validateId(reminder.getId());
        }
        requireNonNull(reminder.getTaskId(), "taskId");
        requireNonNull(reminder.getScheduledAt(), "scheduledAt");
        requireNonNull(reminder.getChannel(), "channel");
        validateTargets(reminder);
    }

    /**
     * Valida el destino del recordatorio según su canal.
     *
     * <p>Para WhatsApp exige {@code targetPhone}; para email exige {@code targetEmail}; para el
     * canal combinado exige ambos. Si el canal no es ninguno de esos, no realiza comprobaciones.
     *
     * @param reminder el recordatorio cuyo destino se valida
     * @throws ValidationException si el destino exigido para el canal está vacío
     */
    private void validateTargets(Reminder reminder) {
        NotificationChannel channel = reminder.getChannel();
        if (channel == NotificationChannel.WHATSAPP) {
            requireText(reminder.getTargetPhone(), "targetPhone");
        } else if (channel == NotificationChannel.EMAIL) {
            requireText(reminder.getTargetEmail(), "targetEmail");
        } else if (channel == NotificationChannel.AMBOS) {
            requireText(reminder.getTargetPhone(), "targetPhone");
            requireText(reminder.getTargetEmail(), "targetEmail");
        }
    }
}
