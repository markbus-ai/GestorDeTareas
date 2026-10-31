package com.trullo.notification;

import com.trullo.exception.NotFoundException;
import com.trullo.interfaces.services.ReminderService;
import com.trullo.interfaces.services.TaskService;
import com.trullo.model.Reminder;
import com.trullo.model.ReminderTarget;
import com.trullo.model.Task;

/**
 * Arma el mensaje que se envía para un destino de recordatorio.
 *
 * <p>El cuerpo nombra la tarea del recordatorio y, si tiene, su fecha de vencimiento. Si el
 * recordatorio no tiene tarea o la tarea ya no existe, usa un texto genérico.
 */
public class ReminderMessageBuilder {
    private static final String TITLE = "Recordatorio de Trullo";
    private static final String DEFAULT_BODY = "Tenés un recordatorio pendiente";

    private final ReminderService reminderService;
    private final TaskService taskService;

    /**
     * Crea el armador de mensajes.
     *
     * @param reminderService el servicio de recordatorios, para obtener el recordatorio del destino
     * @param taskService el servicio de tareas, para obtener la tarea del recordatorio
     */
    public ReminderMessageBuilder(ReminderService reminderService, TaskService taskService) {
        this.reminderService = reminderService;
        this.taskService = taskService;
    }

    /**
     * Arma el mensaje para un destino.
     *
     * @param target el destino del recordatorio
     * @return el mensaje a enviar
     * @throws NotFoundException si el recordatorio del destino no existe
     */
    public Message buildFor(ReminderTarget target) {
        Reminder reminder = reminderService.obtenerPorId(target.getReminderId());
        return new Message(TITLE, buildBody(reminder));
    }

    private String buildBody(Reminder reminder) {
        if (reminder.getTaskId() == null) {
            return DEFAULT_BODY;
        }
        Task task;
        try {
            task = taskService.obtenerPorId(reminder.getTaskId());
        } catch (NotFoundException e) {
            return DEFAULT_BODY;
        }
        if (task.getDueDate() == null) {
            return task.getTitle();
        }
        return task.getTitle() + " (vence el " + task.getDueDate() + ")";
    }
}
