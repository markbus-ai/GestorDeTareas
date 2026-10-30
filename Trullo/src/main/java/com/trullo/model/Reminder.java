package com.trullo.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un recordatorio programado para una tarea.
 *
 * @author Nahuel Romero
 */
public class Reminder {
    private Long id;
    private Long taskId;
    private LocalDateTime scheduledAt;
    private NotificationChannel channel;
    private String targetPhone;
    private String targetEmail;
    private boolean sent;

    /**
     * Crea un recordatorio con sus datos principales.
     *
     * @param id el identificador del recordatorio
     * @param taskId el identificador de la tarea asociada
     * @param scheduledAt la fecha y hora programada
     * @param channel el canal de notificación
     * @param targetPhone el teléfono de destino
     * @param targetEmail el correo de destino
     * @param sent si el recordatorio ya fue enviado
     */
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

    /**
     * Devuelve el identificador del recordatorio.
     *
     * @return el identificador del recordatorio
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del recordatorio.
     *
     * @param id el identificador del recordatorio
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el identificador de la tarea asociada.
     *
     * @return el identificador de la tarea asociada
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * Establece el identificador de la tarea asociada.
     *
     * @param taskId el identificador de la tarea asociada
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * Devuelve la fecha y hora programada.
     *
     * @return la fecha y hora programada
     */
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    /**
     * Establece la fecha y hora programada.
     *
     * @param scheduledAt la fecha y hora programada
     */
    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    /**
     * Devuelve el canal de notificación.
     *
     * @return el canal de notificación
     */
    public NotificationChannel getChannel() {
        return channel;
    }

    /**
     * Establece el canal de notificación.
     *
     * @param channel el canal de notificación
     */
    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    /**
     * Devuelve el teléfono de destino.
     *
     * @return el teléfono de destino
     */
    public String getTargetPhone() {
        return targetPhone;
    }

    /**
     * Establece el teléfono de destino.
     *
     * @param targetPhone el teléfono de destino
     */
    public void setTargetPhone(String targetPhone) {
        this.targetPhone = targetPhone;
    }

    /**
     * Devuelve el correo de destino.
     *
     * @return el correo de destino
     */
    public String getTargetEmail() {
        return targetEmail;
    }

    /**
     * Establece el correo de destino.
     *
     * @param targetEmail el correo de destino
     */
    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    /**
     * Indica si el recordatorio ya fue enviado.
     *
     * @return {@code true} si el recordatorio ya fue enviado
     */
    public boolean isSent() {
        return sent;
    }

    /**
     * Establece si el recordatorio ya fue enviado.
     *
     * @param sent si el recordatorio ya fue enviado
     */
    public void setSent(boolean sent) {
        this.sent = sent;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * {@inheritDoc}
     */
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
