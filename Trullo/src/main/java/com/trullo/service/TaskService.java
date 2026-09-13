package com.trullo.service;

import com.trullo.model.Task;
import com.trullo.model.TaskPriority;
import com.trullo.model.TaskStatus;
import java.time.LocalDate;
import java.util.List;

public interface TaskService {
    Task crear(Task task);

    Task modificar(Task task);

    void eliminar(Long id);

    Task obtenerPorId(Long id);

    List<Task> listar();

    void cambiarEstado(Long id, TaskStatus estado);

    void establecerPrioridad(Long id, TaskPriority prioridad);

    void establecerFechaLimite(Long id, LocalDate fechaLimite);

    List<Task> listarPorProyecto(Long projectId);

    List<Task> filtrarPorEstado(TaskStatus estado);

    List<Task> filtrarPorPrioridad(TaskPriority prioridad);

    List<Task> filtrarPorEtiqueta(Long labelId);

    List<Task> listarProximasAVencer(int dias);

    List<Task> listarVencidas();

    void asociarEtiqueta(Long taskId, Long labelId);

    void quitarEtiqueta(Long taskId, Long labelId);
}
