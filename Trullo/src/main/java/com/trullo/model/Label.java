package com.trullo.model;

import java.util.Objects;

/**
 * Representa una etiqueta que puede asociarse a una o más tareas.
 *
 * @author Nahuel Romero
 */
public class Label {
    private Long id;
    private String name;
    private String color;

    /**
     * Crea una etiqueta con sus datos principales.
     *
     * @param id el identificador de la etiqueta
     * @param name el nombre de la etiqueta
     * @param color el color de la etiqueta
     */
    public Label(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    /**
     * Devuelve el identificador de la etiqueta.
     *
     * @return el identificador de la etiqueta
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador de la etiqueta.
     *
     * @param id el identificador de la etiqueta
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre de la etiqueta.
     *
     * @return el nombre de la etiqueta
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre de la etiqueta.
     *
     * @param name el nombre de la etiqueta
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Devuelve el color de la etiqueta.
     *
     * @return el color de la etiqueta
     */
    public String getColor() {
        return color;
    }

    /**
     * Establece el color de la etiqueta.
     *
     * @param color el color de la etiqueta
     */
    public void setColor(String color) {
        this.color = color;
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
        Label label = (Label) o;
        return Objects.equals(id, label.id);
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
        return "Label{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
