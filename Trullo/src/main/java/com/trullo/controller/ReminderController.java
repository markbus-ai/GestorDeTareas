package com.trullo.controller;

import com.trullo.exception.ValidationException;
import com.trullo.model.Reminder;
import com.trullo.interfaces.BaseInterface;
import com.trullo.interfaces.services.ReminderService;
import java.util.List;

/**
 * Controlador de recordatorios de la aplicación.
 *
 * <p>Valida las entradas correspondientes a recordatorios y delega las operaciones de alta,
 * baja, modificación y consulta en {@link ReminderService}. Un recordatorio requiere
 * {@code taskId} y {@code scheduledAt} no nulos. Sus destinos se gestionan en
 * {@link ReminderTargetController}.
 */
public class ReminderController implements CrudValidator, BaseInterface<Reminder> {
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
     * @throws ValidationException si el recordatorio es nulo o si faltan datos obligatorios
     */
    @Override
    public Reminder create(Reminder reminder) {
        validateReminder(reminder);
        return reminderService.crear(reminder);
    }

    /**
     * Modifica un recordatorio existente.
     *
     * @param reminder el recordatorio con los datos actualizados
     * @return el recordatorio modificado devuelto por el servicio
     * @throws ValidationException si el recordatorio es nulo, si su identificador es nulo o si
     *                             faltan datos obligatorios
     */
    @Override
    public Reminder update(Reminder reminder) {
        validateReminder(reminder);
        validateId(reminder.getId());
        return reminderService.modificar(reminder);
    }

    /**
     * Elimina un recordatorio por su identificador.
     *
     * @param id el identificador del recordatorio a eliminar
     * @throws ValidationException si {@code id} es {@code null}
     */
    @Override
    public void delete(Long id) {
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
    @Override
    public Reminder findById(Long id) {
        validateId(id);
        return reminderService.obtenerPorId(id);
    }

    /**
     * Lista todos los recordatorios.
     *
     * @return la lista de recordatorios devuelta por el servicio
     */
    @Override
    public List<Reminder> findAll() {
        return reminderService.listar();
    }

    /**
     * Valida los datos obligatorios de un recordatorio antes de enviarlo al servicio.
     *
     * @param reminder el recordatorio a validar
     * @throws ValidationException si el recordatorio es nulo o si faltan datos obligatorios
     */
    private void validateReminder(Reminder reminder) {
        requireNonNull(reminder, "reminder");
        requireNonNull(reminder.getTaskId(), "taskId");
        requireNonNull(reminder.getScheduledAt(), "scheduledAt");
    }
}
