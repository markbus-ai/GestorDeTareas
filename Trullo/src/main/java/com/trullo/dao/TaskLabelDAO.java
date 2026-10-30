package com.trullo.dao;

import com.trullo.core.DatabaseConnection;
import com.trullo.exception.DaoException;
import com.trullo.model.Label;
import com.trullo.interfaces.dao.TaskLabelDAOInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar la relación entre tareas y etiquetas.
 *
 * <p>Implementa las operaciones definidas en {@link TaskLabelDAOInterface} para asociar,
 * quitar y consultar etiquetas de una tarea.
 *
 * @author Nahuel Romero
 */
public class TaskLabelDAO implements TaskLabelDAOInterface {

    /**
     * {@inheritDoc}
     *
     * @throws DaoException si falla el acceso a datos
     */
    @Override
    public void addLabel(Long taskId, Long labelId) {
        String sql = "INSERT INTO task_label (task_id, label_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, taskId);
            stmt.setLong(2, labelId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw DaoException.from("Error al agregar etiqueta a tarea", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DaoException si falla el acceso a datos
     */
    @Override
    public void removeLabel(Long taskId, Long labelId) {
        String sql = "DELETE FROM task_label WHERE task_id = ? AND label_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, taskId);
            stmt.setLong(2, labelId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw DaoException.from("Error al quitar etiqueta de tarea", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DaoException si falla el acceso a datos
     */
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
            throw DaoException.from("Error al buscar etiquetas por tarea", e);
        }
    }
}
