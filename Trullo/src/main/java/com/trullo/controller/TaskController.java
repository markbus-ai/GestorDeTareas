package com.trullo.controller;

import com.trullo.exception.ValidationException;
import com.trullo.model.Task;
import com.trullo.model.TaskPriority;
import com.trullo.model.TaskStatus;
import com.trullo.service.TaskService;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de tareas de la aplicación.
 *
 * <p>Valida las entradas correspondientes a tareas y delega las operaciones de alta, baja,
 * modificación, consulta, filtrado y asociación de etiquetas en {@link TaskService}. Una tarea
 * requiere {@code title} y {@code description} no vacíos, y {@code status} y {@code priority}
 * no nulos; la fecha de vencimiento, cuando se informa, no puede ser anterior a la fecha actual.
 */
public class TaskController implements CrudValidator {
    private final TaskService taskService;

    /**
     * Crea un controlador de tareas.
     *
     * @param taskService el servicio de tareas a utilizar; no puede ser {@code null}
     * @throws ValidationException si {@code taskService} es {@code null}
     */
    public TaskController(TaskService taskService) {
        requireNonNull(taskService, "taskService");
        this.taskService = taskService;
    }

    /**
     * Crea una tarea nueva.
     *
     * @param task la tarea a crear
     * @return la tarea creada devuelta por el servicio
     * @throws ValidationException si la tarea es nula, si el título o la descripción están
     *                             vacíos, si el estado o la prioridad son nulos, o si la fecha
     *                             de vencimiento es anterior a la fecha actual
     */
    public Task crear(Task task) {
        validateTask(task, false);
        return taskService.crear(task);
    }

    /**
     * Modifica una tarea existente.
     *
     * @param task la tarea con los datos actualizados
     * @return la tarea modificada devuelta por el servicio
     * @throws ValidationException si la tarea es nula, si su identificador es nulo, si el título
     *                             o la descripción están vacíos, si el estado o la prioridad son
     *                             nulos, o si la fecha de vencimiento es anterior a la fecha actual
     */
    public Task modificar(Task task) {
        validateTask(task, true);
        return taskService.modificar(task);
    }

    /**
     * Elimina una tarea por su identificador.
     *
     * @param id el identificador de la tarea a eliminar
     * @throws ValidationException si {@code id} es {@code null}
     */
    public void eliminar(Long id) {
        validateId(id);
        taskService.eliminar(id);
    }

    /**
     * Obtiene una tarea por su identificador.
     *
     * @param id el identificador de la tarea
     * @return la tarea encontrada
     * @throws ValidationException si {@code id} es {@code null}
     */
    public Task obtenerPorId(Long id) {
        validateId(id);
        return taskService.obtenerPorId(id);
    }

    /**
     * Lista todas las tareas.
     *
     * @return la lista de tareas devuelta por el servicio
     */
    public List<Task> listar() {
        return taskService.listar();
    }

    /**
     * Cambia el estado de una tarea.
     *
     * @param id el identificador de la tarea
     * @param estado el nuevo estado de la tarea
     * @throws ValidationException si {@code id} es {@code null} o si {@code estado} es {@code null}
     */
    public void cambiarEstado(Long id, TaskStatus estado) {
        validateId(id);
        requireNonNull(estado, "estado");
        taskService.cambiarEstado(id, estado);
    }

    /**
     * Cambia la prioridad de una tarea.
     *
     * @param id el identificador de la tarea
     * @param prioridad la nueva prioridad de la tarea
     * @throws ValidationException si {@code id} es {@code null} o si {@code prioridad} es {@code null}
     */
    public void establecerPrioridad(Long id, TaskPriority prioridad) {
        validateId(id);
        requireNonNull(prioridad, "prioridad");
        taskService.establecerPrioridad(id, prioridad);
    }

    /**
     * Establece la fecha de vencimiento de una tarea.
     *
     * @param id el identificador de la tarea
     * @param fechaLimite la fecha de vencimiento a establecer
     * @throws ValidationException si {@code id} es {@code null}, si {@code fechaLimite} es
     *                             {@code null}, o si es anterior a la fecha actual
     */
    public void establecerFechaLimite(Long id, LocalDate fechaLimite) {
        validateId(id);
        validateDueDate(fechaLimite);
        taskService.establecerFechaLimite(id, fechaLimite);
    }

    /**
     * Lista las tareas de un proyecto.
     *
     * @param projectId el identificador del proyecto
     * @return la lista de tareas del proyecto devuelta por el servicio
     * @throws ValidationException si {@code projectId} es {@code null}
     */
    public List<Task> listarPorProyecto(Long projectId) {
        requireNonNull(projectId, "projectId");
        return taskService.listarPorProyecto(projectId);
    }

    /**
     * Filtra las tareas por estado.
     *
     * @param estado el estado por el que filtrar
     * @return la lista de tareas con ese estado devuelta por el servicio
     * @throws ValidationException si {@code estado} es {@code null}
     */
    public List<Task> filtrarPorEstado(TaskStatus estado) {
        requireNonNull(estado, "estado");
        return taskService.filtrarPorEstado(estado);
    }

    /**
     * Filtra las tareas por prioridad.
     *
     * @param prioridad la prioridad por la que filtrar
     * @return la lista de tareas con esa prioridad devuelta por el servicio
     * @throws ValidationException si {@code prioridad} es {@code null}
     */
    public List<Task> filtrarPorPrioridad(TaskPriority prioridad) {
        requireNonNull(prioridad, "prioridad");
        return taskService.filtrarPorPrioridad(prioridad);
    }

    /**
     * Filtra las tareas por etiqueta.
     *
     * @param labelId el identificador de la etiqueta
     * @return la lista de tareas con esa etiqueta devuelta por el servicio
     * @throws ValidationException si {@code labelId} es {@code null}
     */
    public List<Task> filtrarPorEtiqueta(Long labelId) {
        validateId(labelId);
        return taskService.filtrarPorEtiqueta(labelId);
    }

    /**
     * Lista las tareas que vencen dentro de una cantidad de días.
     *
     * @param dias la cantidad de días a considerar; debe ser mayor que cero
     * @return la lista de tareas próximas a vencer devuelta por el servicio
     * @throws ValidationException si {@code dias} es {@code null} o no es mayor que cero
     */
    public List<Task> listarProximasAVencer(int dias) {
        requirePositive(dias, "dias");
        return taskService.listarProximasAVencer(dias);
    }

    /**
     * Lista las tareas vencidas.
     *
     * @return la lista de tareas vencidas devuelta por el servicio
     */
    public List<Task> listarVencidas() {
        return taskService.listarVencidas();
    }

    /**
     * Asocia una etiqueta a una tarea.
     *
     * @param taskId el identificador de la tarea
     * @param labelId el identificador de la etiqueta
     * @throws ValidationException si {@code taskId} o {@code labelId} son {@code null}
     */
    public void asociarEtiqueta(Long taskId, Long labelId) {
        validateId(taskId);
        validateId(labelId);
        taskService.asociarEtiqueta(taskId, labelId);
    }

    /**
     * Quita una etiqueta de una tarea.
     *
     * @param taskId el identificador de la tarea
     * @param labelId el identificador de la etiqueta
     * @throws ValidationException si {@code taskId} o {@code labelId} son {@code null}
     */
    public void quitarEtiqueta(Long taskId, Long labelId) {
        validateId(taskId);
        validateId(labelId);
        taskService.quitarEtiqueta(taskId, labelId);
    }

    /**
     * Valida una tarea antes de enviarla al servicio.
     *
     * @param task la tarea a validar
     * @param requireId indica si debe exigirse un identificador no nulo
     * @throws ValidationException si la tarea es nula, si se exige identificador y este es
     *                             nulo, si el título o la descripción están vacíos, si el estado
     *                             o la prioridad son nulos, o si la fecha de vencimiento es
     *                             anterior a la fecha actual
     */
    // validar el task antes de mandarlo al service, no dejes pasar nulos.
    private void validateTask(Task task, boolean requireId) {
        requireNonNull(task, "task");
        if (requireId) {
            validateId(task.getId());
        }
        requireText(task.getTitle(), "title");
        requireText(task.getDescription(), "description");
        requireNonNull(task.getStatus(), "status");
        requireNonNull(task.getPriority(), "priority");
        if (task.getDueDate() != null) {
            validateDueDate(task.getDueDate());
        }
    }

    /**
     * Valida una fecha de vencimiento.
     *
     * @param dueDate la fecha a validar
     * @throws ValidationException si {@code dueDate} es {@code null} o es anterior a la fecha actual
     */
    // revisá la fecha antes de aceptarla, no aceptes vencidas.
    private void validateDueDate(LocalDate dueDate) {
        requireNonNull(dueDate, "dueDate");
        if (dueDate.isBefore(LocalDate.now())) {
            throw new ValidationException("dueDate no puede ser pasada");
        }
    }
}
