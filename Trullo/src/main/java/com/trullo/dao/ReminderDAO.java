package com.trullo.dao;

import com.trullo.core.DatabaseConnection;
import com.trullo.exception.DaoException;
import com.trullo.model.NotificationChannel;
import com.trullo.model.Reminder;
import com.trullo.interfaces.dao.ReminderDAOInterface;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar el acceso a datos de los recordatorios.
 *
 * <p>Implementa las operaciones CRUD definidas en {@link ReminderDAOInterface} y permite
 * consultar los recordatorios de una tarea y los pendientes de envío.
 *
 * @author Nahuel Romero
 */
public class ReminderDAO implements ReminderDAOInterface {

    /**
     * {@inheritDoc}
     *
     * @throws DaoException si falla el acceso a datos
     */
    @Override
    public Reminder create(Reminder reminder) {
        String sql = "INSERT INTO reminders (task_id, scheduled_at, channel, target_phone, target_email, sent) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, reminder.getTaskId());
            stmt.setTimestamp(2, Timestamp.valueOf(reminder.getScheduledAt()));
            stmt.setString(3, reminder.getChannel().name());
            stmt.setString(4, reminder.getTargetPhone());
            stmt.setString(5, reminder.getTargetEmail());
            stmt.setBoolean(6, reminder.isSent());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    reminder.setId(rs.getLong(1));
                }
            }
            return reminder;
        } catch (SQLException e) {
            throw DaoException.from("Error al crear recordatorio", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DaoException si falla el acceso a datos
     */
    @Override
    public Reminder update(Reminder reminder) {
        String sql = "UPDATE reminders SET task_id = ?, scheduled_at = ?, channel = ?, target_phone = ?, target_email = ?, sent = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, reminder.getTaskId());
            stmt.setTimestamp(2, Timestamp.valueOf(reminder.getScheduledAt()));
            stmt.setString(3, reminder.getChannel().name());
            stmt.setString(4, reminder.getTargetPhone());
            stmt.setString(5, reminder.getTargetEmail());
            stmt.setBoolean(6, reminder.isSent());
            stmt.setLong(7, reminder.getId());
            stmt.executeUpdate();
            return reminder;
        } catch (SQLException e) {
            throw DaoException.from("Error al actualizar recordatorio", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DaoException si falla el acceso a datos
     */
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM reminders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw DaoException.from("Error al eliminar recordatorio", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DaoException si falla el acceso a datos
     */
    @Override
    public Reminder findById(Long id) {
        String sql = "SELECT * FROM reminders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar recordatorio", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DaoException si falla el acceso a datos
     */
    @Override
    public List<Reminder> findAll() {
        String sql = "SELECT * FROM reminders";
        List<Reminder> reminders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reminders.add(mapRow(rs));
            }
            return reminders;
        } catch (SQLException e) {
            throw DaoException.from("Error al listar recordatorios", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DaoException si falla el acceso a datos
     */
    @Override
    public List<Reminder> findByTaskId(Long taskId) {
        String sql = "SELECT * FROM reminders WHERE task_id = ?";
        List<Reminder> reminders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, taskId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reminders.add(mapRow(rs));
            }
            return reminders;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar recordatorios por tarea", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DaoException si falla el acceso a datos
     */
    @Override
    public List<Reminder> findPending() {
        String sql = "SELECT * FROM reminders WHERE sent = false AND scheduled_at <= NOW()";
        List<Reminder> reminders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reminders.add(mapRow(rs));
            }
            return reminders;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar recordatorios pendientes", e);
        }
    }

    private Reminder mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long taskId = rs.getLong("task_id");
        LocalDateTime scheduledAt = rs.getTimestamp("scheduled_at").toLocalDateTime();
        NotificationChannel channel = NotificationChannel.valueOf(rs.getString("channel"));
        String targetPhone = rs.getString("target_phone");
        String targetEmail = rs.getString("target_email");
        boolean sent = rs.getBoolean("sent");
        return new Reminder(id, taskId, scheduledAt, channel, targetPhone, targetEmail, sent);
    }
}
