package com.trullo.interfaces.dao;

import com.trullo.model.Task;
import com.trullo.model.TaskPriority;
import com.trullo.model.TaskStatus;
import java.util.List;
import com.trullo.interfaces.BaseInterface;

/**
 * Define las operaciones de acceso a datos para las tareas.
 * <p>
 * Además del CRUD heredado de {@link BaseInterface}, permite consultar
 * tareas por proyecto, estado, prioridad y etiqueta, y obtener las vencidas
 * o las próximas a vencer.
 *
 * @author Nahuel Romero
 */
public interface TaskDAOInterface extends BaseInterface<Task> {

     /**
     * Busca las tareas que pertenecen a un proyecto.
     *
     * @param projectId identificador del proyecto
     * @return lista de tareas del proyecto; vacía si no hay ninguna
     */
    List<Task> findByProjectId(Long projectId);

     /**
     * Busca las tareas según su estado.
     *
     * @param status estado por el que filtrar
     * @return lista de tareas con ese estado; vacía si no hay ninguna
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Busca las tareas según su prioridad.
     *
     * @param priority prioridad por la que filtrar
     * @return lista de tareas con esa prioridad; vacía si no hay ninguna
     */
    List<Task> findByPriority(TaskPriority priority);

    /**
     * Busca las tareas asociadas a una etiqueta.
     *
     * @param labelId identificador de la etiqueta
     * @return lista de tareas etiquetadas; vacía si no hay ninguna
     */
    List<Task> findByLabelId(Long labelId);
    
    /**
     * Obtiene las tareas cuya fecha límite ya pasó y que no están completadas.
     *
     * @return lista de tareas vencidas; vacía si no hay ninguna
     */
    List<Task> findOverdue();

     /**
     * Obtiene las tareas que vencen entre hoy y los próximos días indicados,
     * excluyendo las completadas.
     *
     * @param days cantidad de días a partir de hoy que se quieren incluir
     * @return lista de tareas próximas a vencer; vacía si no hay ninguna
     */
    List<Task> findDueToday(int days);
}
