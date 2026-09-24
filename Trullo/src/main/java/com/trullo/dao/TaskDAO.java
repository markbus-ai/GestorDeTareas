package com.trullo.dao;

import com.trullo.core.DatabaseConnection;
import com.trullo.exception.DaoException;
import com.trullo.model.Task;
import com.trullo.model.TaskPriority;
import com.trullo.model.TaskStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO implements TaskDAOInterface {

    @Override
    public Task create(Task task) {
        String sql = "INSERT INTO tasks (title, description, status, priority, due_date, project_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setString(4, task.getPriority().name());
            if (task.getDueDate() != null) {
                stmt.setDate(5, Date.valueOf(task.getDueDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            if (task.getProjectId() != null) {
                stmt.setLong(6, task.getProjectId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getLong(1));
                }
            }
            return task;
        } catch (SQLException e) {
            throw DaoException.from("Error al crear tarea", e);
        }
    }

    @Override
    public Task update(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ?, priority = ?, due_date = ?, project_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setString(4, task.getPriority().name());
            if (task.getDueDate() != null) {
                stmt.setDate(5, Date.valueOf(task.getDueDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            if (task.getProjectId() != null) {
                stmt.setLong(6, task.getProjectId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            stmt.setLong(7, task.getId());
            stmt.executeUpdate();
            return task;
        } catch (SQLException e) {
            throw DaoException.from("Error al actualizar tarea", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw DaoException.from("Error al eliminar tarea", e);
        }
    }

    @Override
    public Task findById(Long id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar tarea", e);
        }
    }

    @Override
    public List<Task> findAll() {
        String sql = "SELECT * FROM tasks";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw DaoException.from("Error al listar tareas", e);
        }
    }

    @Override
    public List<Task> findByProjectId(Long projectId) {
        String sql = "SELECT * FROM tasks WHERE project_id = ?";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, projectId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw DaoException.from("Error al listar tareas", e);
        }
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        String sql = "SELECT * FROM tasks WHERE status = ?";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar tareas por estado", e);
        }
    }

    @Override
    public List<Task> findByPriority(TaskPriority priority) {
        String sql = "SELECT * FROM tasks WHERE priority = ?";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, priority.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar tareas por prioridad", e);
        }
    }

    @Override
    public List<Task> findByLabelId(Long labelId) {
        String sql = "SELECT t.* FROM tasks t INNER JOIN task_label tl ON t.id = tl.task_id WHERE tl.label_id = ?";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, labelId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar tareas por etiqueta", e);
        }
    }

    @Override
    public List<Task> findOverdue() {
        String sql = "SELECT * FROM tasks WHERE due_date < CURRENT_DATE AND status != 'COMPLETED'";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar tareas vencidas", e);
        }
    }

    @Override
    public List<Task> findDueToday(int days) {
        String sql = "SELECT * FROM tasks WHERE due_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL ? DAY AND status != 'COMPLETED'";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, days);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapRow(rs));      
            }
            return tasks;
        } catch (SQLException e) {
            throw DaoException.from("Error al buscar tareas para hoy", e);
        }
    }

    private Task mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        TaskStatus status = TaskStatus.valueOf(rs.getString("status"));
        TaskPriority priority = TaskPriority.valueOf(rs.getString("priority"));
        LocalDate dueDate = rs.getDate("due_date") != null
                ? rs.getDate("due_date").toLocalDate()
                : null;
        Long projectId = rs.getLong("project_id");
        if (rs.wasNull()) projectId = null;
        return new Task(id, title, description, status, priority, dueDate, projectId);
    }
}
