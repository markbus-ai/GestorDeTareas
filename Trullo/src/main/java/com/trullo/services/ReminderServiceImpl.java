package com.trullo.services;

import com.trullo.exception.DaoException;
import com.trullo.exception.NotFoundException;
import com.trullo.exception.ServiceException;
import com.trullo.interfaces.dao.ReminderDAOInterface;
import com.trullo.interfaces.dao.ReminderTargetDAOInterface;
import com.trullo.interfaces.services.ReminderService;
import com.trullo.model.Reminder;
import com.trullo.model.ReminderTarget;
import java.util.List;

/**
 * Implementación de {@link ReminderService} que delega la persistencia en
 * {@link ReminderDAOInterface} y {@link ReminderTargetDAOInterface}.
 *
 * <p>Los errores de persistencia se traducen a {@link ServiceException} con un mensaje claro.
 */
public class ReminderServiceImpl implements ReminderService {
    private static final String ENTIDAD = "Recordatorio";

    private final ReminderDAOInterface reminderDAO;
    private final ReminderTargetDAOInterface targetDAO;

    /**
     * Crea el servicio de recordatorios.
     *
     * @param reminderDAO el DAO de recordatorios a utilizar
     * @param targetDAO el DAO de destinos de recordatorios a utilizar
     */
    public ReminderServiceImpl(ReminderDAOInterface reminderDAO, ReminderTargetDAOInterface targetDAO) {
        this.reminderDAO = reminderDAO;
        this.targetDAO = targetDAO;
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

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el recordatorio no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public ReminderTarget agregarDestino(Long reminderId, String channel, String address) {
        obtenerPorId(reminderId);
        try {
            return targetDAO.create(new ReminderTarget(null, reminderId, channel, address, null));
        } catch (DaoException e) {
            throw new ServiceException(
                    "No se pudo agregar el destino al recordatorio con id " + reminderId, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<ReminderTarget> listarDestinos(Long reminderId) {
        try {
            return targetDAO.findByReminderId(reminderId);
        } catch (DaoException e) {
            throw new ServiceException(
                    "No se pudieron listar los destinos del recordatorio con id " + reminderId, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<ReminderTarget> listarDestinosPendientes() {
        try {
            return targetDAO.findPending();
        } catch (DaoException e) {
            throw new ServiceException("No se pudieron listar los destinos pendientes", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void marcarEnviado(Long targetId) {
        try {
            targetDAO.markSent(targetId);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo marcar como enviado el destino con id " + targetId, e);
        }
    }
}
