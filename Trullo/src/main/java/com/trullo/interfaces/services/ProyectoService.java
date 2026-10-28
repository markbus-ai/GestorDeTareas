package com.trullo.interfaces.services;

import com.trullo.model.EstadoProyecto;
import com.trullo.model.Proyecto;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de proyectos de la aplicación.
 *
 * <p>Define las operaciones de negocio de alta, baja, modificación y consulta de proyectos, además
 * del cambio de estado y la gestión de su fecha límite. Las implementaciones se encargan de
 * coordinar la persistencia a través de la capa DAO.
 */
public interface ProyectoService {
    /**
     * Crea un proyecto nuevo.
     *
     * @param proyecto el proyecto a crear
     * @return el proyecto creado, con su identificador asignado
     */
    Proyecto crear(Proyecto proyecto);

    /**
     * Modifica un proyecto existente.
     *
     * @param proyecto el proyecto con los datos actualizados; debe tener identificador
     * @return el proyecto modificado
     */
    Proyecto modificar(Proyecto proyecto);

    /**
     * Elimina un proyecto por su identificador.
     *
     * @param id el identificador del proyecto a eliminar
     */
    void eliminar(Long id);

    /**
     * Obtiene un proyecto por su identificador.
     *
     * @param id el identificador del proyecto
     * @return el proyecto encontrado
     */
    Proyecto obtenerPorId(Long id);

    /**
     * Lista todos los proyectos.
     *
     * @return la lista de proyectos; vacía si no hay ninguno
     */
    List<Proyecto> listar();

    /**
     * Cambia el estado de un proyecto.
     *
     * @param id el identificador del proyecto
     * @param estado el nuevo estado del proyecto
     */
    void cambiarEstado(Long id, EstadoProyecto estado);

    /**
     * Establece la fecha límite de un proyecto.
     *
     * @param id el identificador del proyecto
     * @param fechaLimite la nueva fecha límite del proyecto
     */
    void establecerFechaLimite(Long id, LocalDate fechaLimite);
}
