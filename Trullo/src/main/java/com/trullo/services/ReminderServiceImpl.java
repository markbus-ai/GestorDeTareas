package com.trullo.services;

import com.trullo.exception.NotFoundException;
import com.trullo.interfaces.dao.ReminderDAOInterface;
import com.trullo.interfaces.services.ReminderService;
import com.trullo.model.Reminder;
import java.util.List;

/**
 * Implementación de {@link ReminderService} que delega la persistencia en
 * {@link ReminderDAOInterface}.
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
     */
    @Override
    public Reminder crear(Reminder reminder) {
        return reminderDAO.create(reminder);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el recordatorio no existe
     */
    @Override
    public Reminder modificar(Reminder reminder) {
        obtenerPorId(reminder.getId());
        return reminderDAO.update(reminder);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el recordatorio no existe
     */
    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        reminderDAO.delete(id);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el recordatorio no existe
     */
    @Override
    public Reminder obtenerPorId(Long id) {
        return NotFoundException.requireFound(reminderDAO.findById(id), ENTIDAD, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Reminder> listar() {
        return reminderDAO.findAll();
    }
}
