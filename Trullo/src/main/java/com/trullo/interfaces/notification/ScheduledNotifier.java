package com.trullo.interfaces.notification;

import com.trullo.exception.NotificationException;
import com.trullo.notification.Message;
import java.time.LocalDateTime;

/** Canal cuyo servidor programa el envío al crear el recordatorio. */
public interface ScheduledNotifier extends Notifier {
    void schedule(String address, String taskTitle, LocalDateTime scheduledAt);

    @Override
    default void send(String address, Message message) {
        throw new NotificationException("Este canal se programa al crear el recordatorio");
    }
}
