package com.trullo.interfaces.services;

import com.trullo.model.Label;
import java.util.List;

/**
 * Servicio de etiquetas de la aplicación.
 *
 * <p>Define las operaciones de negocio de alta, baja, modificación y consulta de etiquetas. Las
 * implementaciones se encargan de coordinar la persistencia a través de la capa DAO.
 */
public interface LabelService {
    /**
     * Crea una etiqueta nueva.
     *
     * @param label la etiqueta a crear
     * @return la etiqueta creada, con su identificador asignado
     */
    Label crear(Label label);

    /**
     * Modifica una etiqueta existente.
     *
     * @param label la etiqueta con los datos actualizados; debe tener identificador
     * @return la etiqueta modificada
     */
    Label modificar(Label label);

    /**
     * Elimina una etiqueta por su identificador.
     *
     * @param id el identificador de la etiqueta a eliminar
     */
    void eliminar(Long id);

    /**
     * Obtiene una etiqueta por su identificador.
     *
     * @param id el identificador de la etiqueta
     * @return la etiqueta encontrada
     */
    Label obtenerPorId(Long id);

    /**
     * Lista todas las etiquetas.
     *
     * @return la lista de etiquetas; vacía si no hay ninguna
     */
    List<Label> listar();
}
