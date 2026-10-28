package com.trullo.services;

import com.trullo.exception.NotFoundException;
import com.trullo.interfaces.dao.LabelDAOInterface;
import com.trullo.interfaces.services.LabelService;
import com.trullo.model.Label;
import java.util.List;

/**
 * Implementación de {@link LabelService} que delega la persistencia en {@link LabelDAOInterface}.
 */
public class LabelServiceImpl implements LabelService {
    private static final String ENTIDAD = "Etiqueta";

    private final LabelDAOInterface labelDAO;

    /**
     * Crea el servicio de etiquetas.
     *
     * @param labelDAO el DAO de etiquetas a utilizar
     */
    public LabelServiceImpl(LabelDAOInterface labelDAO) {
        this.labelDAO = labelDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Label crear(Label label) {
        return labelDAO.create(label);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la etiqueta no existe
     */
    @Override
    public Label modificar(Label label) {
        obtenerPorId(label.getId());
        return labelDAO.update(label);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la etiqueta no existe
     */
    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        labelDAO.delete(id);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la etiqueta no existe
     */
    @Override
    public Label obtenerPorId(Long id) {
        return NotFoundException.requireFound(labelDAO.findById(id), ENTIDAD, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Label> listar() {
        return labelDAO.findAll();
    }
}
