package com.trullo.services;

import com.trullo.exception.DaoException;
import com.trullo.exception.NotFoundException;
import com.trullo.exception.ServiceException;
import com.trullo.interfaces.dao.LabelDAOInterface;
import com.trullo.interfaces.services.LabelService;
import com.trullo.model.Label;
import java.util.List;

/**
 * Implementación de {@link LabelService} que delega la persistencia en {@link LabelDAOInterface}.
 *
 * <p>Los errores de persistencia se traducen a {@link ServiceException} con un mensaje claro.
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
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Label crear(Label label) {
        try {
            return labelDAO.create(label);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo crear la etiqueta", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la etiqueta no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Label modificar(Label label) {
        obtenerPorId(label.getId());
        try {
            return labelDAO.update(label);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo modificar la etiqueta con id " + label.getId(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la etiqueta no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        try {
            labelDAO.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo eliminar la etiqueta con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la etiqueta no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Label obtenerPorId(Long id) {
        try {
            return NotFoundException.requireFound(labelDAO.findById(id), ENTIDAD, id);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo obtener la etiqueta con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<Label> listar() {
        try {
            return labelDAO.findAll();
        } catch (DaoException e) {
            throw new ServiceException("No se pudieron listar las etiquetas", e);
        }
    }
}
