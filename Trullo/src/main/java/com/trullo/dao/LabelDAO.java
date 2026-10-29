package com.trullo.dao;

import com.trullo.core.DatabaseConnection;
import com.trullo.exception.DaoException;
import com.trullo.interfaces.dao.LabelDAOInterface;
import com.trullo.model.Label;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar el acceso a datos de las etiquetas de tareas.
 * <p>
 * Implementa las operaciones CRUD definidas en {@link com.trullo.interfaces.BaseInterface}
 * y permite consultar las etiquetas asociadas a una tarea específica.
 *
 * @author Nahuel Romero
 */
public class LabelDAO implements LabelDAOInterface {

    @Override
    public Label create(Label label) {
        String sql = "INSERT INTO labels (name, color) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, label.getName());
            stmt.setString(2, label.getColor());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    label.setId(rs.getLong(1));
                }
            }
            return label;
        } catch (SQLException e) {
            throw DaoException.from("Error al crear etiqueta", e);
        }
    }

    @Override
    public Label update(Label label) {
        String sql = "UPDATE labels SET name = ?, color = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, label.getName());
            stmt.setString(2, label.getColor());
            stmt.setLong(3, label.getId());
            stmt.executeUpdate();
            return label;
        } catch (SQLException e) {
            throw DaoException.from("Error al actualizar etiqueta", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM labels WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw DaoException.from("Error al eliminar etiqueta", e);
        }
    }

    @Override
    public Label findById(Long id) {
        String sql = "SELECT * FROM labels WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar etiqueta", e);
        }
    }

    @Override
    public List<Label> findAll() {
        String sql = "SELECT * FROM labels";
        List<Label> labels = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                labels.add(mapRow(rs));
            }
            return labels;
        } catch (SQLException e) {
            throw DaoException.from("Error al listar etiquetas", e);
        }
    }

    @Override
    public List<Label> findByTaskId(Long taskId) {
        String sql = "SELECT l.* FROM labels l INNER JOIN task_label tl ON l.id = tl.label_id WHERE tl.task_id = ?";
        List<Label> labels = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, taskId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                labels.add(mapRow(rs));
            }
            return labels;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar etiquetas por tarea", e);
        }
    }

    private Label mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String color = rs.getString("color");
        return new Label(id, name, color);
    }
}
