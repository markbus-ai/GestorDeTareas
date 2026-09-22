package com.trullo.controller;

import com.trullo.model.Note;
import com.trullo.service.NoteService;
import java.util.List;

public class NoteController implements CrudValidator {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        requireNonNull(noteService, "noteService");
        this.noteService = noteService;
    }

    public Note crear(Note note) {
        validateNote(note, false);
        return noteService.crear(note);
    }

    public Note modificar(Note note) {
        validateNote(note, true);
        return noteService.modificar(note);
    }

    public void eliminar(Long id) {
        validateId(id);
        noteService.eliminar(id);
    }

    public Note obtenerPorId(Long id) {
        validateId(id);
        return noteService.obtenerPorId(id);
    }

    public List<Note> listar() {
        return noteService.listar();
    }

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
