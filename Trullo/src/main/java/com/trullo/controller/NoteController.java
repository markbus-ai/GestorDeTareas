package com.trullo.controller;

import com.trullo.interfaces.BaseInterface;
import com.trullo.interfaces.services.NoteService;
import com.trullo.model.Note;

import java.util.List;

public class NoteController implements CrudValidator, BaseInterface<Note> {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        requireNonNull(noteService, "noteService");
        this.noteService = noteService;
    }

    @Override
    public Note create(Note note) {
        validateNote(note, false);
        return noteService.crear(note);
    }

    @Override
    public Note update(Note note) {
        validateNote(note, true);
        return noteService.modificar(note);
    }

    @Override
    public void delete(Long id) {
        validateId(id);
        noteService.eliminar(id);
    }

    @Override
    public Note findById(Long id) {
        validateId(id);
        return noteService.obtenerPorId(id);
    }

    @Override
    public List<Note> findAll() {
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
