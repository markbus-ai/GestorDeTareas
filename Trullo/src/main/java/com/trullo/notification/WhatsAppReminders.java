package com.trullo.notification;

import com.trullo.controller.NotificationController;
import com.trullo.controller.WspNotificationController;
import com.trullo.dao.LabelDAO;
import com.trullo.dao.ReminderDAO;
import com.trullo.dao.TaskDAO;
import com.trullo.dao.TaskLabelDAO;
import com.trullo.model.NotificationChannel;
import com.trullo.services.ReminderServiceImpl;
import com.trullo.services.TaskServiceImpl;
import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Path;
import java.util.Map;

/**
 * Arranca el envío de recordatorios por WhatsApp según el archivo {@code .env}.
 *
 * <p>Variables:
 * <ul>
 *   <li>{@code WHATSAPP_DRIVER}: {@code whatsplay} para enviar; cualquier otro valor lo apaga.</li>
 *   <li>{@code WHATSPLAY_DIR}: carpeta de WhatsPlay, con su proyecto {@code uv} y su
 *       {@code whatsapp_session}.</li>
 * </ul>
 *
 * <p>Los canales {@code WHATSAPP} y {@code AMBOS} se envían por WhatsApp. El canal {@code EMAIL}
 * no se registra porque el envío por email todavía no está implementado.
 */
public final class WhatsAppReminders {
    private static final Path SEND_SCRIPT = Path.of("scripts", "send_whatsapp.py");

    private WhatsAppReminders() {
    }

    /**
     * Inicia el envío periódico de recordatorios si está habilitado.
     *
     * @return el planificador iniciado, o {@code null} si el envío está deshabilitado o falta
     *         configurar {@code WHATSPLAY_DIR}
     */
    public static ReminderScheduler start() {
        Dotenv env = Dotenv.configure().ignoreIfMissing().load();
        if (!"whatsplay".equalsIgnoreCase(env.get("WHATSAPP_DRIVER", "").trim())) {
            return null;
        }
        String dir = env.get("WHATSPLAY_DIR", "").trim();
        if (dir.isEmpty()) {
            System.err.println("[recordatorios] falta configurar WHATSPLAY_DIR; no se envían recordatorios");
            return null;
        }

        NotificationController whatsapp =
                new WspNotificationController(WhatsPlaySender.forDirectory(Path.of(dir), SEND_SCRIPT));
        ReminderScheduler scheduler = new ReminderScheduler(
                new ReminderServiceImpl(new ReminderDAO()),
                new TaskServiceImpl(new TaskDAO(), new LabelDAO(), new TaskLabelDAO()),
                Map.of(NotificationChannel.WHATSAPP, whatsapp, NotificationChannel.AMBOS, whatsapp));
        scheduler.start();
        return scheduler;
    }
}
