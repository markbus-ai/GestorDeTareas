package com.trullo.notification;

import com.trullo.exception.NotFoundException;
import com.trullo.interfaces.services.ReminderService;
import com.trullo.interfaces.services.TaskService;
import com.trullo.model.Reminder;
import com.trullo.model.ReminderTarget;
import com.trullo.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Arma el mensaje que se envía para un destino de recordatorio.
 *
 * <p>El cuerpo nombra la tarea del recordatorio. Si el
 * recordatorio no tiene tarea o la tarea ya no existe, usa un texto genérico.
 */
public class ReminderMessageBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderMessageBuilder.class);
    private static final String TITLE = "Recordatorio de Trullo";
    private static final String DEFAULT_TASK = "una tarea";

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
        return "🔔 *Recordatorio de Trullo*\n\nTenés pendiente: " + taskTitle(reminder)
                + "\n\nUn pequeño avance también cuenta. ¡Es momento de darle lugar!";
    }

    private String taskTitle(Reminder reminder) {
        if (reminder.getTaskId() == null) {
            return DEFAULT_TASK;
        }
        Task task;
        try {
            task = taskService.obtenerPorId(reminder.getTaskId());
        } catch (NotFoundException e) {
            LOGGER.warn("No se encontro la tarea del recordatorio " + reminder.getId()
                    + "; se usa el mensaje generico", e);
            return DEFAULT_TASK;
        }
        return task.getTitle();
    }
}
