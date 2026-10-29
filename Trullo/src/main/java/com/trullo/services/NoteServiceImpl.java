package com.trullo.services;

import com.trullo.exception.DaoException;
import com.trullo.exception.NotFoundException;
import com.trullo.exception.ServiceException;
import com.trullo.interfaces.dao.NoteDAOInterface;
import com.trullo.interfaces.services.NoteService;
import com.trullo.model.Note;
import java.util.List;

/**
 * Implementación de {@link NoteService} que delega la persistencia en {@link NoteDAOInterface}.
 *
 * <p>Los errores de persistencia se traducen a {@link ServiceException} con un mensaje claro.
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
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Note crear(Note note) {
        try {
            return noteDAO.create(note);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo crear la nota", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la nota no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Note modificar(Note note) {
        obtenerPorId(note.getId());
        try {
            return noteDAO.update(note);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo modificar la nota con id " + note.getId(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la nota no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        try {
            noteDAO.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo eliminar la nota con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la nota no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Note obtenerPorId(Long id) {
        try {
            return NotFoundException.requireFound(noteDAO.findById(id), ENTIDAD, id);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo obtener la nota con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<Note> listar() {
        try {
            return noteDAO.findAll();
        } catch (DaoException e) {
            throw new ServiceException("No se pudieron listar las notas", e);
        }
    }
}
