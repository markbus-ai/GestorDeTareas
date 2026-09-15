package com.trullo.dao;

import com.trullo.core.DatabaseConnection;
import com.trullo.model.Label;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskLabelDAO implements TaskLabelDAOInterface {

    @Override
    public void addLabel(Long taskId, Long labelId) {
        String sql = "INSERT INTO task_label (task_id, label_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, taskId);
            stmt.setLong(2, labelId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar etiqueta a tarea", e);
        }
    }

    @Override
    public void removeLabel(Long taskId, Long labelId) {
        String sql = "DELETE FROM task_label WHERE task_id = ? AND label_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, taskId);
            stmt.setLong(2, labelId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al quitar etiqueta de tarea", e);
        }
    }

    @Override
    public List<Label> findLabelsByTaskId(Long taskId) {
        String sql = "SELECT l.* FROM labels l INNER JOIN task_label tl ON l.id = tl.label_id WHERE tl.task_id = ?";
        List<Label> labels = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, taskId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                String color = rs.getString("color");
                labels.add(new Label(id, name, color));
            }
            return labels;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar etiquetas por tarea", e);
        }
    }
}
