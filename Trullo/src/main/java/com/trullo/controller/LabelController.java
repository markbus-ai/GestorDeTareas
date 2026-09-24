package com.trullo.controller;

import com.trullo.model.Label;
import com.trullo.service.LabelService;
import java.util.List;

/**
 * Controlador de etiquetas de la aplicación.
 *
 * <p>Valida las entradas correspondientes a etiquetas y delega las operaciones de alta, baja,
 * modificación y consulta en {@link LabelService}. Toda etiqueta debe tener {@code name} y
 * {@code color} no vacíos.
 */
public class LabelController implements CrudValidator {
    private final LabelService labelService;

    /**
     * Crea un controlador de etiquetas.
     *
     * @param labelService el servicio de etiquetas a utilizar; no puede ser {@code null}
     * @throws ValidationException si {@code labelService} es {@code null}
     */
    public LabelController(LabelService labelService) {
        requireNonNull(labelService, "labelService");
        this.labelService = labelService;
    }

    /**
     * Crea una etiqueta nueva.
     *
     * @param label la etiqueta a crear
     * @return la etiqueta creada devuelta por el servicio
     * @throws ValidationException si la etiqueta es nula o si {@code name} o {@code color} están vacíos
     */
    public Label crear(Label label) {
        validateLabel(label, false);
        return labelService.crear(label);
    }

    /**
     * Modifica una etiqueta existente.
     *
     * @param label la etiqueta con los datos actualizados
     * @return la etiqueta modificada devuelta por el servicio
     * @throws ValidationException si la etiqueta es nula, si su identificador es nulo, o si
     *                             {@code name} o {@code color} están vacíos
     */
    public Label modificar(Label label) {
        validateLabel(label, true);
        return labelService.modificar(label);
    }

    /**
     * Elimina una etiqueta por su identificador.
     *
     * @param id el identificador de la etiqueta a eliminar
     * @throws ValidationException si {@code id} es {@code null}
     */
    public void eliminar(Long id) {
        validateId(id);
        labelService.eliminar(id);
    }

    /**
     * Obtiene una etiqueta por su identificador.
     *
     * @param id el identificador de la etiqueta
     * @return la etiqueta encontrada
     * @throws ValidationException si {@code id} es {@code null}
     */
    public Label obtenerPorId(Long id) {
        validateId(id);
        return labelService.obtenerPorId(id);
    }

    /**
     * Lista todas las etiquetas.
     *
     * @return la lista de etiquetas devuelta por el servicio
     */
    public List<Label> listar() {
        return labelService.listar();
    }

    /**
     * Valida una etiqueta antes de enviarla al servicio.
     *
     * @param label la etiqueta a validar
     * @param requireId indica si debe exigirse un identificador no nulo
     * @throws ValidationException si la etiqueta es nula, si se exige identificador y este es
     *                             nulo, o si {@code name} o {@code color} están vacíos
     */
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
