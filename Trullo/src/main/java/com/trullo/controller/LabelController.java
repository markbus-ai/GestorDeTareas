package com.trullo.controller;

import com.trullo.exception.ValidationException;
import com.trullo.model.Label;
import com.trullo.service.LabelService;
import java.util.List;

public class LabelController implements CrudValidator {
    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        if (labelService == null) {
            throw new ValidationException("labelService no puede ser nulo");
        }
        this.labelService = labelService;
    }

    public Label crear(Label label) {
        validateLabel(label, false);
        return labelService.crear(label);
    }

    public Label modificar(Label label) {
        validateLabel(label, true);
        return labelService.modificar(label);
    }

    public void eliminar(Long id) {
        validateId(id);
        labelService.eliminar(id);
    }

    public Label obtenerPorId(Long id) {
        validateId(id);
        return labelService.obtenerPorId(id);
    }

    public List<Label> listar() {
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
