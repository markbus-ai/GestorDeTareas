package com.trullo.notification;

import com.trullo.controller.NotificationController;
import com.trullo.exception.NotFoundException;
import com.trullo.exception.NotificationException;
import com.trullo.exception.ServiceException;
import com.trullo.exception.ValidationException;
import com.trullo.interfaces.services.ReminderService;
import com.trullo.interfaces.services.TaskService;
import com.trullo.model.NotificationChannel;
import com.trullo.model.Reminder;
import com.trullo.model.Task;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Envía los recordatorios vencidos que todavía no se enviaron.
 *
 * <p>Cada minuto pide los recordatorios pendientes a {@link ReminderService} y envía cada uno con
 * el {@link NotificationController} registrado para su canal. Los recordatorios cuyo canal no
 * tiene un controlador registrado se ignoran. Los que se envían bien se marcan como enviados; los
 * que fallan quedan pendientes y se reintentan en la próxima pasada.
 */
public class ReminderScheduler {
    private static final long INTERVAL_SECONDS = 60;
    private static final String TITLE = "Recordatorio de Trullo";
    private static final String DEFAULT_CONTENT = "Tenés un recordatorio pendiente";

    private final ReminderService reminderService;
    private final TaskService taskService;
    private final Map<NotificationChannel, NotificationController> channels;
    private ScheduledExecutorService executor;

    /**
     * Crea el planificador de recordatorios.
     *
     * @param reminderService el servicio de recordatorios
     * @param taskService el servicio de tareas, usado para armar el texto del mensaje
     * @param channels el controlador de notificaciones que se usa para cada canal
     */
    public ReminderScheduler(ReminderService reminderService, TaskService taskService,
                             Map<NotificationChannel, NotificationController> channels) {
        this.reminderService = reminderService;
        this.taskService = taskService;
        this.channels = new EnumMap<>(channels);
    }

    /**
     * Inicia los envíos periódicos en un hilo en segundo plano.
     */
    public synchronized void start() {
        if (executor != null) {
            return;
        }
        executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "reminder-scheduler");
            thread.setDaemon(true);
            return thread;
        });
        executor.scheduleWithFixedDelay(this::tick, 0, INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Detiene los envíos periódicos.
     */
    public synchronized void stop() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    /**
     * Envía una vez los recordatorios pendientes.
     *
     * @return la cantidad de recordatorios enviados y marcados como enviados
     * @throws ServiceException si no se pueden obtener los recordatorios pendientes
     */
    public int sendPending() {
        int sent = 0;
        for (Reminder reminder : reminderService.listarPendientes()) {
            NotificationController controller = channels.get(reminder.getChannel());
            if (controller == null) {
                continue;
            }
            try {
                controller.sendNotification(TITLE, buildContent(reminder), destinationOf(reminder));
                reminder.setSent(true);
                reminderService.modificar(reminder);
                sent++;
            } catch (ValidationException | NotificationException | ServiceException | NotFoundException e) {
                System.err.println("[recordatorios] no se pudo enviar el recordatorio " + reminder.getId()
                        + ": " + e.getMessage());
            }
        }
        return sent;
    }

    private void tick() {
        try {
            sendPending();
        } catch (Throwable t) {
            // An uncaught throwable would silently cancel every later run of the schedule.
            System.err.println("[recordatorios] falló la pasada de envío: " + t);
        }
    }

    private static String destinationOf(Reminder reminder) {
        return reminder.getChannel() == NotificationChannel.EMAIL
                ? reminder.getTargetEmail()
                : reminder.getTargetPhone();
    }

    private String buildContent(Reminder reminder) {
        if (reminder.getTaskId() == null) {
            return DEFAULT_CONTENT;
        }
        Task task;
        try {
            task = taskService.obtenerPorId(reminder.getTaskId());
        } catch (NotFoundException e) {
            return DEFAULT_CONTENT;
        }
        String content = task.getTitle();
        if (task.getDueDate() != null) {
            content += " (vence el " + task.getDueDate() + ")";
        }
        return content;
    }
}
