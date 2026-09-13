package com.trullo.service;

import com.trullo.model.Label;
import java.util.List;

public interface LabelService {
    Label crear(Label label);

    Label modificar(Label label);

    void eliminar(Long id);

    Label obtenerPorId(Long id);

    List<Label> listar();
}
