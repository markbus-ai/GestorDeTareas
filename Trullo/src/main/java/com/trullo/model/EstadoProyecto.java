package com.trullo.model;

/**
 * Estados posibles de un proyecto.
 *
 * @author Nahuel Romero
 */
public enum EstadoProyecto {
    /** El proyecto está en curso. */
    ACTIVO,
    /** El proyecto está temporalmente detenido. */
    PAUSADO,
    /** El proyecto fue completado. */
    FINALIZADO,
    /** El proyecto fue cancelado. */
    CANCELADO
}
