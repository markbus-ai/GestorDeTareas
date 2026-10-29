package com.trullo.controller;

import com.trullo.interfaces.BaseInterface;
import com.trullo.interfaces.services.LabelService;
import com.trullo.model.Label;

import java.util.List;

public class LabelController implements CrudValidator, BaseInterface<Label> {
    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        requireNonNull(labelService, "labelService");
        this.labelService = labelService;
    }

    @Override
    public Label create(Label label) {
        validateLabel(label, false);
        return labelService.crear(label);
    }

    @Override
    public Label update(Label label) {
        validateLabel(label, true);
        return labelService.modificar(label);
    }

    @Override
    public void delete(Long id) {
        validateId(id);
        labelService.eliminar(id);
    }

    @Override
    public Label findById(Long id) {
        validateId(id);
        return labelService.obtenerPorId(id);
    }

    @Override
    public List<Label> findAll() {
        return labelService.listar();
    }

    // validar el label antes de mandarlo al service, no dejes pasar nulos.
    // pedí name y color siempre, no aceptes vacíos.
    private void validateLabel(Label label, boolean requireId) {
        requireNonNull(label, "label");
        if (requireId) {
            validateId(label.getId());
        }
        requireText(label.getName(), "name");
        requireText(label.getColor(), "color");
    }
}
