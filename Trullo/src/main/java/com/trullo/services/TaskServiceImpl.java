package com.trullo.services;

import com.trullo.exception.NotFoundException;
import com.trullo.interfaces.dao.LabelDAOInterface;
import com.trullo.interfaces.dao.TaskDAOInterface;
import com.trullo.interfaces.dao.TaskLabelDAOInterface;
import com.trullo.interfaces.services.TaskService;
import com.trullo.model.Task;
import com.trullo.model.TaskPriority;
import com.trullo.model.TaskStatus;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de {@link TaskService} que delega la persistencia en {@link TaskDAOInterface} y
 * la asociación con etiquetas en {@link TaskLabelDAOInterface}.
 */
public class TaskServiceImpl implements TaskService {
    private static final String ENTIDAD = "Tarea";
    private static final String ENTIDAD_ETIQUETA = "Etiqueta";

    private final TaskDAOInterface taskDAO;
    private final LabelDAOInterface labelDAO;
    private final TaskLabelDAOInterface taskLabelDAO;

    /**
     * Crea el servicio de tareas.
     *
     * @param taskDAO el DAO de tareas a utilizar
     * @param labelDAO el DAO de etiquetas, usado para validar que una etiqueta exista
     * @param taskLabelDAO el DAO de la relación entre tareas y etiquetas
     */
    public TaskServiceImpl(TaskDAOInterface taskDAO, LabelDAOInterface labelDAO,
                           TaskLabelDAOInterface taskLabelDAO) {
        this.taskDAO = taskDAO;
        this.labelDAO = labelDAO;
        this.taskLabelDAO = taskLabelDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task crear(Task task) {
        return taskDAO.create(task);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     */
    @Override
    public Task modificar(Task task) {
        obtenerPorId(task.getId());
        return taskDAO.update(task);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     */
    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        taskDAO.delete(id);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     */
    @Override
    public Task obtenerPorId(Long id) {
        return NotFoundException.requireFound(taskDAO.findById(id), ENTIDAD, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> listar() {
        return taskDAO.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     */
    @Override
    public void cambiarEstado(Long id, TaskStatus estado) {
        Task task = obtenerPorId(id);
        task.setStatus(estado);
        taskDAO.update(task);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     */
    @Override
    public void establecerPrioridad(Long id, TaskPriority prioridad) {
        Task task = obtenerPorId(id);
        task.setPriority(prioridad);
        taskDAO.update(task);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     */
    @Override
    public void establecerFechaLimite(Long id, LocalDate fechaLimite) {
        Task task = obtenerPorId(id);
        task.setDueDate(fechaLimite);
        taskDAO.update(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> listarPorProyecto(Long projectId) {
        return taskDAO.findByProjectId(projectId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> filtrarPorEstado(TaskStatus estado) {
        return taskDAO.findByStatus(estado);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> filtrarPorPrioridad(TaskPriority prioridad) {
        return taskDAO.findByPriority(prioridad);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> filtrarPorEtiqueta(Long labelId) {
        return taskDAO.findByLabelId(labelId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> listarProximasAVencer(int dias) {
        return taskDAO.findDueToday(dias);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> listarVencidas() {
        return taskDAO.findOverdue();
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea o la etiqueta no existen
     */
    @Override
    public void asociarEtiqueta(Long taskId, Long labelId) {
        obtenerPorId(taskId);
        NotFoundException.requireFound(labelDAO.findById(labelId), ENTIDAD_ETIQUETA, labelId);
        taskLabelDAO.addLabel(taskId, labelId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void quitarEtiqueta(Long taskId, Long labelId) {
        taskLabelDAO.removeLabel(taskId, labelId);
    }
}
