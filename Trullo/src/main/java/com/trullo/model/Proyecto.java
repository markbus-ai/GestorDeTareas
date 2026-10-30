package com.trullo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un proyecto que agrupa tareas.
 *
 * @author Nahuel Romero
 */
public class Proyecto {
    private Long id;
    private String name;
    private String description;
    private EstadoProyecto status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;

    /**
     * Crea un proyecto con sus datos principales.
     *
     * @param id el identificador del proyecto
     * @param name el nombre del proyecto
     * @param description la descripción del proyecto
     * @param status el estado del proyecto
     * @param dueDate la fecha límite del proyecto
     */
    public Proyecto(Long id, String name, String description, EstadoProyecto status, LocalDate dueDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

    /**
     * Devuelve el identificador del proyecto.
     *
     * @return el identificador del proyecto
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del proyecto.
     *
     * @param id el identificador del proyecto
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre del proyecto.
     *
     * @return el nombre del proyecto
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del proyecto.
     *
     * @param name el nombre del proyecto
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Devuelve la descripción del proyecto.
     *
     * @return la descripción del proyecto
     */
    public String getDescription() {
        return description;
    }

    /**
     * Establece la descripción del proyecto.
     *
     * @param description la descripción del proyecto
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Devuelve el estado del proyecto.
     *
     * @return el estado del proyecto
     */
    public EstadoProyecto getStatus() {
        return status;
    }

    /**
     * Establece el estado del proyecto.
     *
     * @param status el estado del proyecto
     */
    public void setStatus(EstadoProyecto status) {
        this.status = status;
    }

    /**
     * Devuelve la fecha límite del proyecto.
     *
     * @return la fecha límite del proyecto
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Establece la fecha límite del proyecto.
     *
     * @param dueDate la fecha límite del proyecto
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Devuelve la fecha de creación del proyecto.
     *
     * @return la fecha de creación del proyecto
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Establece la fecha de creación del proyecto.
     *
     * @param createdAt la fecha de creación del proyecto
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
        Proyecto proyecto = (Proyecto) o;
        return Objects.equals(id, proyecto.id);
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
        return "Proyecto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                '}';
    }
}
