package com.trullo.services;

import com.trullo.exception.DaoException;
import com.trullo.exception.NotFoundException;
import com.trullo.exception.ServiceException;
import com.trullo.interfaces.dao.ReminderTargetDAOInterface;
import com.trullo.interfaces.services.ReminderService;
import com.trullo.interfaces.services.ReminderTargetService;
import com.trullo.model.ReminderTarget;
import com.trullo.interfaces.services.TaskService;
import com.trullo.interfaces.notification.ScheduledNotifier;
import com.trullo.notification.NotifierRegistry;
import java.util.List;

/**
 * Implementación de {@link ReminderTargetService} que delega la persistencia en
 * {@link ReminderTargetDAOInterface}.
 *
 * <p>Los errores de persistencia se traducen a {@link ServiceException} con un mensaje claro.
 */
public class ReminderTargetServiceImpl implements ReminderTargetService {
    private final ReminderTargetDAOInterface targetDAO;
    private final ReminderService reminderService;
    private final TaskService taskService;
    private final NotifierRegistry notifiers;

    /**
     * Crea el servicio de destinos de recordatorios.
     *
     * @param targetDAO el DAO de destinos de recordatorios a utilizar
     * @param reminderService el servicio de recordatorios, usado para verificar que el
     *                        recordatorio exista antes de agregarle un destino
     */
    public ReminderTargetServiceImpl(ReminderTargetDAOInterface targetDAO, ReminderService reminderService,
                                    TaskService taskService, NotifierRegistry notifiers) {
        this.targetDAO = targetDAO;
        this.reminderService = reminderService;
        this.taskService = taskService;
        this.notifiers = notifiers;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el recordatorio no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public ReminderTarget agregarDestino(Long reminderId, String channel, String address) {
        var reminder = reminderService.obtenerPorId(reminderId);
        var notifier = notifiers.find(channel).orElseThrow(() ->
                new com.trullo.exception.ValidationException("El canal " + channel + " no esta configurado"));
        notifier.validate(address);
        String title = notifier instanceof ScheduledNotifier
                ? taskService.obtenerPorId(reminder.getTaskId()).getTitle() : null;
        ReminderTarget target = null;
        try {
            target = targetDAO.create(new ReminderTarget(null, reminderId, channel, address, null));
            if (notifier instanceof ScheduledNotifier scheduled) {
                scheduled.schedule(address, title, reminder.getScheduledAt());
            }
            return target;
        } catch (RuntimeException e) {
            if (target != null) {
                try {
                    targetDAO.delete(target.getId());
                } catch (RuntimeException cleanup) {
                    e.addSuppressed(cleanup);
                }
            }
            if (e instanceof DaoException) {
                throw new ServiceException("No se pudo agregar el destino al recordatorio con id " + reminderId, e);
            }
            throw e;
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
