package com.trullo.dao;

import com.trullo.core.DatabaseConnection;
import com.trullo.model.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO implements NoteDAOInterface {

    @Override
    public Note create(Note note) {
        String sql = "INSERT INTO notes (title, content) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, note.getTitle());
            stmt.setString(2, note.getContent());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                note.setId(rs.getLong(1));
            }
            return note;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear nota", e);
        }
    }

    @Override
    public Note update(Note note) {
        String sql = "UPDATE notes SET title = ?, content = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, note.getTitle());
            stmt.setString(2, note.getContent());
            stmt.setLong(3, note.getId());
            stmt.executeUpdate();
            return note;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar nota", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar nota", e);
        }
    }

    @Override
    public Note findById(Long id) {
        String sql = "SELECT * FROM notes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar nota", e);
        }
    }

    @Override
    public List<Note> findAll() {
        String sql = "SELECT * FROM notes";
        List<Note> notes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notes.add(mapRow(rs));
            }
            return notes;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar notas", e);
        }
    }

    private Note mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String title = rs.getString("title");
        String content = rs.getString("content");
        return new Note(id, title, content);
    }
}
