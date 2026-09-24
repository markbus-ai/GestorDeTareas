package com.trullo.dao;

import com.trullo.core.DatabaseConnection;
import com.trullo.model.EstadoProyecto;
import com.trullo.model.Proyecto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO implements ProjectDAOInterface {

    @Override
    public Proyecto create(Proyecto proyecto) {
        String sql = "INSERT INTO projects (name, description, status, due_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, proyecto.getName());
            stmt.setString(2, proyecto.getDescription());
            stmt.setString(3, proyecto.getStatus().name());
            stmt.setObject(4, proyecto.getDueDate());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                proyecto.setId(rs.getLong(1));
            }
            return proyecto;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear proyecto", e);
        }
    }

    @Override
    public Proyecto update(Proyecto proyecto) {
        String sql = "UPDATE projects SET name = ?, description = ?, status = ?, due_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, proyecto.getName());
            stmt.setString(2, proyecto.getDescription());
            stmt.setString(3, proyecto.getStatus().name());
            stmt.setObject(4, proyecto.getDueDate());
            stmt.setLong(5, proyecto.getId());
            stmt.executeUpdate();
            return proyecto;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar proyecto", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar proyecto", e);
        }
    }

    @Override
    public Proyecto findById(Long id) {
        String sql = "SELECT * FROM projects WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar proyecto", e);
        }
    }

    @Override
    public List<Proyecto> findAll() {
        String sql = "SELECT * FROM projects";
        List<Proyecto> proyectos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                proyectos.add(mapRow(rs));
            }
            return proyectos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar proyectos", e);
        }
    }

    private Proyecto mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        EstadoProyecto status = EstadoProyecto.valueOf(rs.getString("status"));
        LocalDate dueDate = rs.getDate("due_date") != null
                ? rs.getDate("due_date").toLocalDate()
                : null;
        return new Proyecto(id, name, description, status, dueDate);
    }
}
