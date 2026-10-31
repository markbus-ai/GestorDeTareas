package com.trullo.services;

import com.trullo.exception.DaoException;
import com.trullo.exception.NotFoundException;
import com.trullo.exception.ServiceException;
import com.trullo.interfaces.dao.ReminderDAOInterface;
import com.trullo.interfaces.services.ReminderService;
import com.trullo.model.Reminder;
import java.util.List;

/**
 * Implementación de {@link ReminderService} que delega la persistencia en
 * {@link ReminderDAOInterface}.
 *
 * <p>Los errores de persistencia se traducen a {@link ServiceException} con un mensaje claro.
 */
public class ReminderServiceImpl implements ReminderService {
    private static final String ENTIDAD = "Recordatorio";

    private final ReminderDAOInterface reminderDAO;

    /**
     * Crea el servicio de recordatorios.
     *
     * @param reminderDAO el DAO de recordatorios a utilizar
     */
    public ReminderServiceImpl(ReminderDAOInterface reminderDAO) {
        this.reminderDAO = reminderDAO;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Reminder crear(Reminder reminder) {
        try {
            return reminderDAO.create(reminder);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo crear el recordatorio", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el recordatorio no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Reminder modificar(Reminder reminder) {
        obtenerPorId(reminder.getId());
        try {
            return reminderDAO.update(reminder);
        } catch (DaoException e) {
            throw new ServiceException(
                    "No se pudo modificar el recordatorio con id " + reminder.getId(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el recordatorio no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        try {
            reminderDAO.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo eliminar el recordatorio con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el recordatorio no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Reminder obtenerPorId(Long id) {
        try {
            return NotFoundException.requireFound(reminderDAO.findById(id), ENTIDAD, id);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo obtener el recordatorio con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<Reminder> listar() {
        try {
            return reminderDAO.findAll();
        } catch (DaoException e) {
            throw new ServiceException("No se pudieron listar los recordatorios", e);
        }
    }
}
