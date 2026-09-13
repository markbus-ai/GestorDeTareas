package com.trullo.service;

import com.trullo.model.Note;
import java.util.List;

public interface NoteService {
    Note crear(Note note);

    Note modificar(Note note);

    void eliminar(Long id);

    Note obtenerPorId(Long id);

    List<Note> listar();
}
