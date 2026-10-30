package com.trullo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa una tarea del gestor, con su estado, prioridad y fechas.
 *
 * @author Nahuel Romero
 */
public class Task {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private Long projectId;

    /**
     * Crea una tarea con sus datos principales.
     *
     * @param id el identificador de la tarea
     * @param title el título de la tarea
     * @param description la descripción de la tarea
     * @param status el estado de la tarea
     * @param priority la prioridad de la tarea
     * @param dueDate la fecha límite de la tarea
     * @param projectId el identificador del proyecto asociado
     */
    public Task(Long id, String title, String description, TaskStatus status,
                TaskPriority priority, LocalDate dueDate, Long projectId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.projectId = projectId;
    }

    /**
     * Devuelve el identificador de la tarea.
     *
     * @return el identificador de la tarea
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador de la tarea.
     *
     * @param id el identificador de la tarea
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el título de la tarea.
     *
     * @return el título de la tarea
     */
    public String getTitle() {
        return title;
    }

    /**
     * Establece el título de la tarea.
     *
     * @param title el título de la tarea
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Devuelve la descripción de la tarea.
     *
     * @return la descripción de la tarea
     */
    public String getDescription() {
        return description;
    }

    /**
     * Establece la descripción de la tarea.
     *
     * @param description la descripción de la tarea
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Devuelve el estado de la tarea.
     *
     * @return el estado de la tarea
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Establece el estado de la tarea.
     *
     * @param status el estado de la tarea
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * Devuelve la prioridad de la tarea.
     *
     * @return la prioridad de la tarea
     */
    public TaskPriority getPriority() {
        return priority;
    }

    /**
     * Establece la prioridad de la tarea.
     *
     * @param priority la prioridad de la tarea
     */
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    /**
     * Devuelve la fecha límite de la tarea.
     *
     * @return la fecha límite de la tarea
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Establece la fecha límite de la tarea.
     *
     * @param dueDate la fecha límite de la tarea
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Devuelve la fecha de creación de la tarea.
     *
     * @return la fecha de creación de la tarea
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Establece la fecha de creación de la tarea.
     *
     * @param createdAt la fecha de creación de la tarea
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Devuelve el identificador del proyecto asociado.
     *
     * @return el identificador del proyecto asociado
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Establece el identificador del proyecto asociado.
     *
     * @param projectId el identificador del proyecto asociado
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", projectId=" + projectId +
                '}';
    }
}
