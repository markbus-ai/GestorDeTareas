package com.trullo.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Destino de un recordatorio: el canal por el que se envía y la dirección dentro de ese canal.
 *
 * <p>El canal es el identificador de texto del notificador (por ejemplo {@code whatsapp} o
 * {@code email}), así que agregar un canal nuevo no requiere tocar este modelo. Un destino está
 * enviado cuando tiene {@code sentAt}.
 */
public class ReminderTarget {
    private Long id;
    private Long reminderId;
    private String channel;
    private String address;
    private LocalDateTime sentAt;

    public ReminderTarget(Long id, Long reminderId, String channel, String address, LocalDateTime sentAt) {
        this.id = id;
        this.reminderId = reminderId;
        this.channel = channel;
        this.address = address;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReminderId() {
        return reminderId;
    }

    public void setReminderId(Long reminderId) {
        this.reminderId = reminderId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    /**
     * Indica si el recordatorio ya se envió a este destino.
     *
     * @return {@code true} si tiene fecha de envío
     */
    public boolean isSent() {
        return sentAt != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReminderTarget target = (ReminderTarget) o;
        return Objects.equals(id, target.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ReminderTarget{" +
                "id=" + id +
                ", reminderId=" + reminderId +
                ", channel='" + channel + '\'' +
                ", address='" + address + '\'' +
                ", sentAt=" + sentAt +
                '}';
    }
}
