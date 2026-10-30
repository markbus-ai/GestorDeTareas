package com.trullo.model;

/**
 * Estados posibles de una tarea.
 *
 * @author Nahuel Romero
 */
public enum TaskStatus {
    /** La tarea todavía no fue iniciada. */
    PENDIENTE,
    /** La tarea está en curso. */
    EN_PROGRESO,
    /** La tarea fue finalizada. */
    COMPLETADA,
    /** La tarea superó su fecha límite sin completarse. */
    ATRASADA
}
