package com.trullo.controller;

import com.trullo.exception.ValidationException;
import com.trullo.model.NotificationChannel;
import com.trullo.model.Reminder;
import com.trullo.service.ReminderService;
import java.util.List;

public class ReminderController implements CrudValidator {
    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        requireNonNull(reminderService, "reminderService");
        this.reminderService = reminderService;
    }

    public Reminder crear(Reminder reminder) {
        validateReminder(reminder, false);
        return reminderService.crear(reminder);
    }

    public Reminder modificar(Reminder reminder) {
        validateReminder(reminder, true);
        return reminderService.modificar(reminder);
    }

    public void eliminar(Long id) {
        validateId(id);
        reminderService.eliminar(id);
    }

    public Reminder obtenerPorId(Long id) {
        validateId(id);
        return reminderService.obtenerPorId(id);
    }

    public List<Reminder> listar() {
        return reminderService.listar();
    }

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
