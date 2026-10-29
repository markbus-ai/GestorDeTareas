package com.trullo.interfaces.dao;

import com.trullo.model.Reminder;
import java.util.List;
import com.trullo.interfaces.BaseInterface;

/**
 * Define las operaciones de acceso a datos para los Recordatorios.
 *
 * @author Nahuel Romero
 */
public interface ReminderDAOInterface extends BaseInterface<Reminder> {
    /**
     * Operacion para buscar recordatorios mediante una tarea
     * 
     * @param taskId identificador de la tarea 
     * @return Devuelve una lista de Recordatorios; en caso de no haber devulve la lista vacia
     */
    List<Reminder> findByTaskId(Long taskId);

    /**
     * Operacion para buscar Recordatorios pendientes
     * 
     * @return Devuelve una lista de Recordatorios; en caso de no haber devulve la lista vacia
     */
    List<Reminder> findPending();
}
