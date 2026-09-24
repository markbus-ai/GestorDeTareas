package com.trullo.controller;

import com.trullo.model.Note;
import com.trullo.service.NoteService;
import java.util.List;

/**
 * Controlador de notas de la aplicación.
 *
 * <p>Valida las entradas correspondientes a notas y delega las operaciones de alta, baja,
 * modificación y consulta en {@link NoteService}. Toda nota debe tener {@code title} y
 * {@code content} no vacíos.
 */
public class NoteController implements CrudValidator {
    private final NoteService noteService;

    /**
     * Crea un controlador de notas.
     *
     * @param noteService el servicio de notas a utilizar; no puede ser {@code null}
     * @throws ValidationException si {@code noteService} es {@code null}
     */
    public NoteController(NoteService noteService) {
        requireNonNull(noteService, "noteService");
        this.noteService = noteService;
    }

    /**
     * Crea una nota nueva.
     *
     * @param note la nota a crear
     * @return la nota creada devuelta por el servicio
     * @throws ValidationException si la nota es nula o si {@code title} o {@code content} están vacíos
     */
    public Note crear(Note note) {
        validateNote(note, false);
        return noteService.crear(note);
    }

    /**
     * Modifica una nota existente.
     *
     * @param note la nota con los datos actualizados
     * @return la nota modificada devuelta por el servicio
     * @throws ValidationException si la nota es nula, si su identificador es nulo, o si
     *                             {@code title} o {@code content} están vacíos
     */
    public Note modificar(Note note) {
        validateNote(note, true);
        return noteService.modificar(note);
    }

    /**
     * Elimina una nota por su identificador.
     *
     * @param id el identificador de la nota a eliminar
     * @throws ValidationException si {@code id} es {@code null}
     */
    public void eliminar(Long id) {
        validateId(id);
        noteService.eliminar(id);
    }

    /**
     * Obtiene una nota por su identificador.
     *
     * @param id el identificador de la nota
     * @return la nota encontrada
     * @throws ValidationException si {@code id} es {@code null}
     */
    public Note obtenerPorId(Long id) {
        validateId(id);
        return noteService.obtenerPorId(id);
    }

    /**
     * Lista todas las notas.
     *
     * @return la lista de notas devuelta por el servicio
     */
    public List<Note> listar() {
        return noteService.listar();
    }

    /**
     * Valida una nota antes de enviarla al servicio.
     *
     * @param note la nota a validar
     * @param requireId indica si debe exigirse un identificador no nulo
     * @throws ValidationException si la nota es nula, si se exige identificador y este es
     *                             nulo, o si {@code title} o {@code content} están vacíos
     */
    // validar la note antes de mandarla al service, no dejes pasar nulos.
    // pedí title y content siempre, no aceptes vacíos.
    private void validateNote(Note note, boolean requireId) {
        requireNonNull(note, "note");
        if (requireId) {
            validateId(note.getId());
        }
        requireText(note.getTitle(), "title");
        requireText(note.getContent(), "content");
    }
}
