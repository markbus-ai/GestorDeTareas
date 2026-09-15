package com.trullo.controller;

import com.trullo.exception.ValidationException;
import com.trullo.model.Task;
import com.trullo.model.TaskPriority;
import com.trullo.model.TaskStatus;
import com.trullo.service.TaskService;
import java.time.LocalDate;
import java.util.List;

public class TaskController implements CrudValidator {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        requireNonNull(taskService, "taskService");
        this.taskService = taskService;
    }

    public Task crear(Task task) {
        validateTask(task, false);
        return taskService.crear(task);
    }

    public Task modificar(Task task) {
        validateTask(task, true);
        return taskService.modificar(task);
    }

    public void eliminar(Long id) {
        validateId(id);
        taskService.eliminar(id);
    }

    public Task obtenerPorId(Long id) {
        validateId(id);
        return taskService.obtenerPorId(id);
    }

    public List<Task> listar() {
        return taskService.listar();
    }

    public void cambiarEstado(Long id, TaskStatus estado) {
        validateId(id);
        requireNonNull(estado, "estado");
        taskService.cambiarEstado(id, estado);
    }

    public void establecerPrioridad(Long id, TaskPriority prioridad) {
        validateId(id);
        requireNonNull(prioridad, "prioridad");
        taskService.establecerPrioridad(id, prioridad);
    }

    public void establecerFechaLimite(Long id, LocalDate fechaLimite) {
        validateId(id);
        validateDueDate(fechaLimite);
        taskService.establecerFechaLimite(id, fechaLimite);
    }

    public List<Task> listarPorProyecto(Long projectId) {
        requireNonNull(projectId, "projectId");
        return taskService.listarPorProyecto(projectId);
    }

    public List<Task> filtrarPorEstado(TaskStatus estado) {
        requireNonNull(estado, "estado");
        return taskService.filtrarPorEstado(estado);
    }

    public List<Task> filtrarPorPrioridad(TaskPriority prioridad) {
        requireNonNull(prioridad, "prioridad");
        return taskService.filtrarPorPrioridad(prioridad);
    }

    public List<Task> filtrarPorEtiqueta(Long labelId) {
        validateId(labelId);
        return taskService.filtrarPorEtiqueta(labelId);
    }

    public List<Task> listarProximasAVencer(int dias) {
        requirePositive(dias, "dias");
        return taskService.listarProximasAVencer(dias);
    }

    public List<Task> listarVencidas() {
        return taskService.listarVencidas();
    }

    public void asociarEtiqueta(Long taskId, Long labelId) {
        validateId(taskId);
        validateId(labelId);
        taskService.asociarEtiqueta(taskId, labelId);
    }

    public void quitarEtiqueta(Long taskId, Long labelId) {
        validateId(taskId);
        validateId(labelId);
        taskService.quitarEtiqueta(taskId, labelId);
    }

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

    // revisá la fecha antes de aceptarla, no aceptes vencidas.
    private void validateDueDate(LocalDate dueDate) {
        requireNonNull(dueDate, "dueDate");
        if (dueDate.isBefore(LocalDate.now())) {
            throw new ValidationException("dueDate no puede ser pasada");
        }
    }
}
