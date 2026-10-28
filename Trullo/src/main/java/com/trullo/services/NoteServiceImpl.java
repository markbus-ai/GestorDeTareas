package com.trullo.services;

import com.trullo.exception.NotFoundException;
import com.trullo.interfaces.dao.NoteDAOInterface;
import com.trullo.interfaces.services.NoteService;
import com.trullo.model.Note;
import java.util.List;

/**
 * Implementación de {@link NoteService} que delega la persistencia en {@link NoteDAOInterface}.
 */
public class NoteServiceImpl implements NoteService {
    private static final String ENTIDAD = "Nota";

    private final NoteDAOInterface noteDAO;

    /**
     * Crea el servicio de notas.
     *
     * @param noteDAO el DAO de notas a utilizar
     */
    public NoteServiceImpl(NoteDAOInterface noteDAO) {
        this.noteDAO = noteDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Note crear(Note note) {
        return noteDAO.create(note);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la nota no existe
     */
    @Override
    public Note modificar(Note note) {
        obtenerPorId(note.getId());
        return noteDAO.update(note);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la nota no existe
     */
    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        noteDAO.delete(id);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la nota no existe
     */
    @Override
    public Note obtenerPorId(Long id) {
        return NotFoundException.requireFound(noteDAO.findById(id), ENTIDAD, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Note> listar() {
        return noteDAO.findAll();
    }
}
