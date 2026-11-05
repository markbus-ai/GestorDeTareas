package com.trullo.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Recordatorio de una tarea programado para una fecha y hora.
 *
 * <p>Los canales y direcciones a los que se envía viven en {@link ReminderTarget}.
 */
public class Reminder {
    private Long id;
    private Long taskId;
    private LocalDateTime scheduledAt;

    public Reminder(Long id, Long taskId, LocalDateTime scheduledAt) {
        this.id = id;
        this.taskId = taskId;
        this.scheduledAt = scheduledAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reminder reminder = (Reminder) o;
        return Objects.equals(id, reminder.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", scheduledAt=" + scheduledAt +
                '}';
    }
}
