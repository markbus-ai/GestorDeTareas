package com.trullo.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reminder {
    private Long id;
    private Long taskId;
    private LocalDateTime scheduledAt;
    private NotificationChannel channel;
    private String targetPhone;
    private String targetEmail;
    private boolean sent;

    public Reminder(Long id, Long taskId, LocalDateTime scheduledAt,
                    NotificationChannel channel, String targetPhone, String targetEmail, boolean sent) {
        this.id = id;
        this.taskId = taskId;
        this.scheduledAt = scheduledAt;
        this.channel = channel;
        this.targetPhone = targetPhone;
        this.targetEmail = targetEmail;
        this.sent = sent;
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

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public String getTargetPhone() {
        return targetPhone;
    }

    public void setTargetPhone(String targetPhone) {
        this.targetPhone = targetPhone;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
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
                ", channel=" + channel +
                ", targetPhone='" + targetPhone + '\'' +
                ", targetEmail='" + targetEmail + '\'' +
                ", sent=" + sent +
                '}';
    }
}
