package com.trullo.dao;

import com.trullo.core.DatabaseConnection;
import com.trullo.exception.DaoException;
import com.trullo.interfaces.dao.ReminderTargetDAOInterface;
import com.trullo.model.ReminderTarget;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReminderTargetDAO implements ReminderTargetDAOInterface {

    @Override
    public ReminderTarget create(ReminderTarget target) {
        String sql = "INSERT INTO reminder_targets (reminder_id, channel, address, sent_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, target.getReminderId());
            stmt.setString(2, target.getChannel());
            stmt.setString(3, target.getAddress());
            setSentAt(stmt, 4, target.getSentAt());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    target.setId(rs.getLong(1));
                }
            }
            return target;
        } catch (SQLException e) {
            throw DaoException.from("Error al crear destino de recordatorio", e);
        }
    }

    @Override
    public ReminderTarget update(ReminderTarget target) {
        String sql = "UPDATE reminder_targets SET reminder_id = ?, channel = ?, address = ?, sent_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, target.getReminderId());
            stmt.setString(2, target.getChannel());
            stmt.setString(3, target.getAddress());
            setSentAt(stmt, 4, target.getSentAt());
            stmt.setLong(5, target.getId());
            stmt.executeUpdate();
            return target;
        } catch (SQLException e) {
            throw DaoException.from("Error al actualizar destino de recordatorio", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM reminder_targets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw DaoException.from("Error al eliminar destino de recordatorio", e);
        }
    }

    @Override
    public ReminderTarget findById(Long id) {
        String sql = "SELECT * FROM reminder_targets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar destino de recordatorio", e);
        }
    }

    @Override
    public List<ReminderTarget> findAll() {
        String sql = "SELECT * FROM reminder_targets";
        List<ReminderTarget> targets = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                targets.add(mapRow(rs));
            }
            return targets;
        } catch (SQLException e) {
            throw DaoException.from("Error al listar destinos de recordatorios", e);
        }
    }

    @Override
    public List<ReminderTarget> findByReminderId(Long reminderId) {
        String sql = "SELECT * FROM reminder_targets WHERE reminder_id = ?";
        List<ReminderTarget> targets = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, reminderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                targets.add(mapRow(rs));
            }
            return targets;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar destinos por recordatorio", e);
        }
    }

    @Override
    public List<ReminderTarget> findPending() {
        String sql = "SELECT rt.* FROM reminder_targets rt INNER JOIN reminders r ON r.id = rt.reminder_id "
                + "WHERE rt.sent_at IS NULL AND r.scheduled_at <= NOW()";
        List<ReminderTarget> targets = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                targets.add(mapRow(rs));
            }
            return targets;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar destinos pendientes", e);
        }
    }

    @Override
    public void markSent(Long id) {
        String sql = "UPDATE reminder_targets SET sent_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw DaoException.from("Error al marcar destino como enviado", e);
        }
    }

    private void setSentAt(PreparedStatement stmt, int index, LocalDateTime sentAt) throws SQLException {
        if (sentAt != null) {
            stmt.setTimestamp(index, Timestamp.valueOf(sentAt));
        } else {
            stmt.setNull(index, Types.TIMESTAMP);
        }
    }

    private ReminderTarget mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long reminderId = rs.getLong("reminder_id");
        String channel = rs.getString("channel");
        String address = rs.getString("address");
        Timestamp sentAtTimestamp = rs.getTimestamp("sent_at");
        LocalDateTime sentAt = sentAtTimestamp != null ? sentAtTimestamp.toLocalDateTime() : null;
        return new ReminderTarget(id, reminderId, channel, address, sentAt);
    }
}
