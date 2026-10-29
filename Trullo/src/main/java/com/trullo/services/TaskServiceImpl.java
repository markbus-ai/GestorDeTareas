package com.trullo.services;

import com.trullo.exception.DaoException;
import com.trullo.exception.NotFoundException;
import com.trullo.exception.ServiceException;
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
 *
 * <p>Los errores de persistencia se traducen a {@link ServiceException} con un mensaje claro.
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
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Task crear(Task task) {
        try {
            return taskDAO.create(task);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo crear la tarea", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Task modificar(Task task) {
        obtenerPorId(task.getId());
        try {
            return taskDAO.update(task);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo modificar la tarea con id " + task.getId(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        try {
            taskDAO.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo eliminar la tarea con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Task obtenerPorId(Long id) {
        try {
            return NotFoundException.requireFound(taskDAO.findById(id), ENTIDAD, id);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo obtener la tarea con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<Task> listar() {
        try {
            return taskDAO.findAll();
        } catch (DaoException e) {
            throw new ServiceException("No se pudieron listar las tareas", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void cambiarEstado(Long id, TaskStatus estado) {
        Task task = obtenerPorId(id);
        task.setStatus(estado);
        try {
            taskDAO.update(task);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo cambiar el estado de la tarea con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void establecerPrioridad(Long id, TaskPriority prioridad) {
        Task task = obtenerPorId(id);
        task.setPriority(prioridad);
        try {
            taskDAO.update(task);
        } catch (DaoException e) {
            throw new ServiceException(
                    "No se pudo establecer la prioridad de la tarea con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void establecerFechaLimite(Long id, LocalDate fechaLimite) {
        Task task = obtenerPorId(id);
        task.setDueDate(fechaLimite);
        try {
            taskDAO.update(task);
        } catch (DaoException e) {
            throw new ServiceException(
                    "No se pudo establecer la fecha límite de la tarea con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<Task> listarPorProyecto(Long projectId) {
        try {
            return taskDAO.findByProjectId(projectId);
        } catch (DaoException e) {
            throw new ServiceException(
                    "No se pudieron listar las tareas del proyecto con id " + projectId, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<Task> filtrarPorEstado(TaskStatus estado) {
        try {
            return taskDAO.findByStatus(estado);
        } catch (DaoException e) {
            throw new ServiceException("No se pudieron filtrar las tareas por estado", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<Task> filtrarPorPrioridad(TaskPriority prioridad) {
        try {
            return taskDAO.findByPriority(prioridad);
        } catch (DaoException e) {
            throw new ServiceException("No se pudieron filtrar las tareas por prioridad", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<Task> filtrarPorEtiqueta(Long labelId) {
        try {
            return taskDAO.findByLabelId(labelId);
        } catch (DaoException e) {
            throw new ServiceException("No se pudieron filtrar las tareas por etiqueta", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<Task> listarProximasAVencer(int dias) {
        try {
            return taskDAO.findDueToday(dias);
        } catch (DaoException e) {
            throw new ServiceException("No se pudieron listar las tareas próximas a vencer", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<Task> listarVencidas() {
        try {
            return taskDAO.findOverdue();
        } catch (DaoException e) {
            throw new ServiceException("No se pudieron listar las tareas vencidas", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si la tarea o la etiqueta no existen
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void asociarEtiqueta(Long taskId, Long labelId) {
        obtenerPorId(taskId);
        try {
            NotFoundException.requireFound(labelDAO.findById(labelId), ENTIDAD_ETIQUETA, labelId);
            taskLabelDAO.addLabel(taskId, labelId);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo asociar la etiqueta a la tarea con id " + taskId, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void quitarEtiqueta(Long taskId, Long labelId) {
        try {
            taskLabelDAO.removeLabel(taskId, labelId);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo quitar la etiqueta de la tarea con id " + taskId, e);
        }
    }
}
