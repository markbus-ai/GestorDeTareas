package com.trullo.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa una nota de texto creada por el usuario.
 *
 * @author Nahuel Romero
 */
public class Note {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    /**
     * Crea una nota con sus datos principales.
     *
     * @param id el identificador de la nota
     * @param title el título de la nota
     * @param content el contenido de la nota
     */
    public Note(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    /**
     * Devuelve el identificador de la nota.
     *
     * @return el identificador de la nota
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador de la nota.
     *
     * @param id el identificador de la nota
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el título de la nota.
     *
     * @return el título de la nota
     */
    public String getTitle() {
        return title;
    }

    /**
     * Establece el título de la nota.
     *
     * @param title el título de la nota
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Devuelve el contenido de la nota.
     *
     * @return el contenido de la nota
     */
    public String getContent() {
        return content;
    }

    /**
     * Establece el contenido de la nota.
     *
     * @param content el contenido de la nota
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Devuelve la fecha de creación de la nota.
     *
     * @return la fecha de creación de la nota
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Establece la fecha de creación de la nota.
     *
     * @param createdAt la fecha de creación de la nota
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
        Note note = (Note) o;
        return Objects.equals(id, note.id);
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
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
