package com.trullo.interfaces.services;

import com.trullo.model.Note;
import java.util.List;

/**
 * Servicio de notas de la aplicación.
 *
 * <p>Define las operaciones de negocio de alta, baja, modificación y consulta de notas. Las
 * implementaciones se encargan de coordinar la persistencia a través de la capa DAO.
 */
public interface NoteService {
    /**
     * Crea una nota nueva.
     *
     * @param note la nota a crear
     * @return la nota creada, con su identificador asignado
     */
    Note crear(Note note);

    /**
     * Modifica una nota existente.
     *
     * @param note la nota con los datos actualizados; debe tener identificador
     * @return la nota modificada
     */
    Note modificar(Note note);

    /**
     * Elimina una nota por su identificador.
     *
     * @param id el identificador de la nota a eliminar
     */
    void eliminar(Long id);

    /**
     * Obtiene una nota por su identificador.
     *
     * @param id el identificador de la nota
     * @return la nota encontrada
     */
    Note obtenerPorId(Long id);

    /**
     * Lista todas las notas.
     *
     * @return la lista de notas; vacía si no hay ninguna
     */
    List<Note> listar();
}
