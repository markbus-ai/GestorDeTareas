package com.trullo.notification;

import com.trullo.exception.NotFoundException;
import com.trullo.exception.NotificationException;
import com.trullo.exception.ServiceException;
import com.trullo.exception.ValidationException;
import com.trullo.interfaces.notification.Notifier;
import com.trullo.interfaces.services.ReminderService;
import com.trullo.interfaces.services.TaskService;
import com.trullo.model.Reminder;
import com.trullo.model.ReminderTarget;
import com.trullo.model.Task;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Envía los destinos de recordatorios vencidos que todavía no se enviaron.
 *
 * <p>Cada minuto pide los destinos pendientes a {@link ReminderService} y envía cada uno con el
 * {@link Notifier} de su canal, que busca en el {@link NotifierRegistry}. Los destinos cuyo canal
 * no está disponible se saltean. Los que se envían bien se marcan como enviados; los que fallan
 * quedan pendientes y se reintentan en la próxima pasada.
 */
public class ReminderScheduler {
    private static final long INTERVAL_SECONDS = 60;
    private static final String TITLE = "Recordatorio de Trullo";
    private static final String DEFAULT_BODY = "Tenés un recordatorio pendiente";

    private final ReminderService reminderService;
    private final TaskService taskService;
    private final NotifierRegistry notifiers;
    private ScheduledExecutorService executor;

    /**
     * Crea el planificador de recordatorios.
     *
     * @param reminderService el servicio de recordatorios
     * @param taskService el servicio de tareas, usado para armar el texto del mensaje
     * @param notifiers los canales de notificación disponibles
     */
    public ReminderScheduler(ReminderService reminderService, TaskService taskService,
                             NotifierRegistry notifiers) {
        this.reminderService = reminderService;
        this.taskService = taskService;
        this.notifiers = notifiers;
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
     * Envía una vez los destinos pendientes.
     *
     * @return la cantidad de destinos enviados y marcados como enviados
     * @throws ServiceException si no se pueden obtener los destinos pendientes
     */
    public int sendPending() {
        int sentCount = 0;
        for (ReminderTarget target : reminderService.listarDestinosPendientes()) {
            if (trySend(target)) {
                sentCount++;
            }
        }
        return sentCount;
    }

    private boolean trySend(ReminderTarget target) {
        Optional<Notifier> notifier = notifiers.find(target.getChannel());
        if (notifier.isEmpty()) {
            System.err.println("[recordatorios] el canal " + target.getChannel()
                    + " no está configurado; se saltea el destino " + target.getId());
            return false;
        }
        try {
            // Targets may come straight from the database, bypassing ReminderController's validation.
            notifier.get().validate(target.getAddress());
            notifier.get().send(target.getAddress(), new Message(TITLE, buildBody(target)));
            reminderService.marcarEnviado(target.getId());
            return true;
        } catch (ValidationException | NotificationException | ServiceException | NotFoundException e) {
            System.err.println("[recordatorios] no se pudo enviar el destino " + target.getId()
                    + ": " + e.getMessage());
            return false;
        }
    }

    private void tick() {
        try {
            sendPending();
        } catch (Throwable t) {
            // An uncaught throwable would silently cancel every later run of the schedule.
            System.err.println("[recordatorios] falló la pasada de envío: " + t);
        }
    }

    private String buildBody(ReminderTarget target) {
        Reminder reminder = reminderService.obtenerPorId(target.getReminderId());
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
