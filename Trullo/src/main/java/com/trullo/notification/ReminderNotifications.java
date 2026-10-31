package com.trullo.notification;

import com.trullo.dao.LabelDAO;
import com.trullo.dao.ReminderDAO;
import com.trullo.dao.ReminderTargetDAO;
import com.trullo.dao.TaskDAO;
import com.trullo.dao.TaskLabelDAO;
import com.trullo.interfaces.services.ReminderService;
import com.trullo.interfaces.services.TaskService;
import com.trullo.services.ReminderServiceImpl;
import com.trullo.services.ReminderTargetServiceImpl;
import com.trullo.services.TaskServiceImpl;
import io.github.cdimascio.dotenv.Dotenv;

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
    public static ReminderScheduler start() {
        Dotenv env = Dotenv.configure().ignoreIfMissing().load();
        NotifierRegistry notifiers = NotifierRegistry.fromServiceLoader(env);
        if (notifiers.isEmpty()) {
            return null;
        }
        ReminderService reminderService = new ReminderServiceImpl(new ReminderDAO());
        TaskService taskService = new TaskServiceImpl(new TaskDAO(), new LabelDAO(), new TaskLabelDAO());
        ReminderScheduler scheduler = new ReminderScheduler(
                new ReminderTargetServiceImpl(new ReminderTargetDAO(), reminderService),
                new ReminderMessageBuilder(reminderService, taskService),
                notifiers);
        scheduler.start();
        return scheduler;
    }
}
