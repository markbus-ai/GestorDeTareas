package com.trullo.interfaces.services;

import com.trullo.model.Task;
import com.trullo.model.TaskPriority;
import com.trullo.model.TaskStatus;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de tareas de la aplicación.
 *
 * <p>Define las operaciones de negocio de alta, baja, modificación y consulta de tareas, junto con
 * la gestión de estado, prioridad y fecha límite, los filtros de búsqueda y la asociación de
 * etiquetas. Las implementaciones se encargan de coordinar la persistencia a través de la capa DAO.
 */
public interface TaskService {
    /**
     * Crea una tarea nueva.
     *
     * @param task la tarea a crear
     * @return la tarea creada, con su identificador asignado
     */
    Task crear(Task task);

    /**
     * Modifica una tarea existente.
     *
     * @param task la tarea con los datos actualizados; debe tener identificador
     * @return la tarea modificada
     */
    Task modificar(Task task);

    /**
     * Elimina una tarea por su identificador.
     *
     * @param id el identificador de la tarea a eliminar
     */
    void eliminar(Long id);

    /**
     * Obtiene una tarea por su identificador.
     *
     * @param id el identificador de la tarea
     * @return la tarea encontrada
     */
    Task obtenerPorId(Long id);

    /**
     * Lista todas las tareas.
     *
     * @return la lista de tareas; vacía si no hay ninguna
     */
    List<Task> listar();

    /**
     * Cambia el estado de una tarea.
     *
     * @param id el identificador de la tarea
     * @param estado el nuevo estado de la tarea
     */
    void cambiarEstado(Long id, TaskStatus estado);

    /**
     * Establece la prioridad de una tarea.
     *
     * @param id el identificador de la tarea
     * @param prioridad la nueva prioridad de la tarea
     */
    void establecerPrioridad(Long id, TaskPriority prioridad);

    /**
     * Establece la fecha límite de una tarea.
     *
     * @param id el identificador de la tarea
     * @param fechaLimite la nueva fecha límite de la tarea
     */
    void establecerFechaLimite(Long id, LocalDate fechaLimite);

    /**
     * Lista las tareas que pertenecen a un proyecto.
     *
     * @param projectId el identificador del proyecto
     * @return las tareas del proyecto; vacía si no tiene ninguna
     */
    List<Task> listarPorProyecto(Long projectId);

    /**
     * Filtra las tareas por estado.
     *
     * @param estado el estado por el cual filtrar
     * @return las tareas que tienen ese estado
     */
    List<Task> filtrarPorEstado(TaskStatus estado);

    /**
     * Filtra las tareas por prioridad.
     *
     * @param prioridad la prioridad por la cual filtrar
     * @return las tareas que tienen esa prioridad
     */
    List<Task> filtrarPorPrioridad(TaskPriority prioridad);

    /**
     * Filtra las tareas que tienen asociada una etiqueta.
     *
     * @param labelId el identificador de la etiqueta
     * @return las tareas asociadas a esa etiqueta
     */
    List<Task> filtrarPorEtiqueta(Long labelId);

    /**
     * Lista las tareas cuya fecha límite vence dentro de los próximos días indicados.
     *
     * @param dias la cantidad de días hacia adelante a considerar desde hoy
     * @return las tareas próximas a vencer
     */
    List<Task> listarProximasAVencer(int dias);

    /**
     * Lista las tareas cuya fecha límite ya pasó.
     *
     * @return las tareas vencidas
     */
    List<Task> listarVencidas();

    /**
     * Asocia una etiqueta a una tarea.
     *
     * @param taskId el identificador de la tarea
     * @param labelId el identificador de la etiqueta a asociar
     */
    void asociarEtiqueta(Long taskId, Long labelId);

    /**
     * Quita la asociación entre una etiqueta y una tarea.
     *
     * @param taskId el identificador de la tarea
     * @param labelId el identificador de la etiqueta a quitar
     */
    void quitarEtiqueta(Long taskId, Long labelId);
}
