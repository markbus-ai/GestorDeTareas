package com.trullo.notification;

import com.trullo.core.Container;
import com.trullo.interfaces.services.ReminderService;
import com.trullo.interfaces.services.ReminderTargetService;
import com.trullo.interfaces.services.TaskService;

/**
 * Arranca el envío de recordatorios con los canales configurados en el archivo {@code .env}.
 *
 * <p>Cada canal decide si está configurado a través de su {@link
 * com.trullo.interfaces.notification.NotifierFactory}, así que esta clase no conoce ningún canal
 * concreto.
 */
public final class ReminderNotifications {

    private ReminderNotifications() {
    }

    /**
     * Inicia el envío periódico de recordatorios si hay al menos un canal configurado.
     *
     * @return el planificador iniciado, o {@code null} si no hay ningún canal configurado
     */
    public static ReminderScheduler start(Container container) {
        NotifierRegistry notifiers = container.resolve(NotifierRegistry.class);
        if (notifiers.isEmpty()) {
            return null;
        }
        ReminderService reminderService = container.resolve(ReminderService.class);
        TaskService taskService = container.resolve(TaskService.class);
        ReminderScheduler scheduler = new ReminderScheduler(
                container.resolve(ReminderTargetService.class),
                new ReminderMessageBuilder(reminderService, taskService),
                notifiers);
        scheduler.start();
        return scheduler;
    }
}
