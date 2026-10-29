package com.trullo.interfaces.dao;

import com.trullo.model.Label;
import java.util.List;
import com.trullo.interfaces.BaseDAOInterface;

/**
 * Define las operaciones de acceso a datos para las etiquetas de tareas.
 *
 * @author Nahuel Romero
 */
public interface LabelDAOInterface extends BaseDAOInterface<Label> {
    /**
     * Busca las etiquetas asociadas a una tarea.
     *
     * @param taskId identificador de la tarea
     * @return lista de etiquetas asociadas a la tarea; vacía si no hay ninguna
     */
    List<Label> findByTaskId(Long taskId);
}
