package com.trullo.interfaces.dao;

import com.trullo.model.Label;
import java.util.List;

/**
 * Define las operaciones de acceso a datos para la relación entre tareas
 * y etiquetas.
 *
 * @author Nahuel Romero
 */
public interface TaskLabelDAOInterface {

    /**
     * Asocia una etiqueta a una tarea.
     *
     * @param taskId identificador de la tarea
     * @param labelId identificador de la etiqueta
     */
    void addLabel(Long taskId, Long labelId);

    /**
     * Quita la asociación entre una etiqueta y una tarea.
     *
     * @param taskId identificador de la tarea
     * @param labelId identificador de la etiqueta
     */
    void removeLabel(Long taskId, Long labelId);

    /**
     * Obtiene las etiquetas asociadas a una tarea.
     *
     * @param taskId identificador de la tarea
     * @return lista de etiquetas de la tarea; vacía si no tiene ninguna
     */
    List<Label> findLabelsByTaskId(Long taskId);
}
