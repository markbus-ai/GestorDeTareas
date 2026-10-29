package com.trullo.interfaces.dao;

import com.trullo.model.Reminder;
import java.util.List;
import com.trullo.interfaces.BaseInterface;

/**
 * Define las operaciones de acceso a datos para los recordatorios.
 * <p>
 * Además del CRUD heredado de {@link BaseInterface}, permite consultar
 * los recordatorios de una tarea y los que están pendientes de envío.
 *
 * @author Nahuel Romero
 */
public interface ReminderDAOInterface extends BaseInterface<Reminder> {

    /**
     * Busca los recordatorios asociados a una tarea.
     *
     * @param taskId identificador de la tarea
     * @return lista de recordatorios de la tarea; vacía si no hay ninguno
     */
    List<Reminder> findByTaskId(Long taskId);

    /**
     * Obtiene los recordatorios que aún no fueron enviados y cuya hora
     * programada ya se cumplió.
     *
     * @return lista de recordatorios pendientes; vacía si no hay ninguno
     */
    List<Reminder> findPending();
}
