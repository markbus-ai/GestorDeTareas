package com.trullo.controller;

import com.trullo.exception.ValidationException;
import com.trullo.model.EstadoProyecto;
import com.trullo.model.Proyecto;
import com.trullo.service.ProyectoService;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de proyectos de la aplicación.
 *
 * <p>Valida las entradas correspondientes a proyectos y delega las operaciones de alta, baja,
 * modificación, consulta y cambios de estado o fecha límite en {@link ProyectoService}. Un
 * proyecto requiere un {@code nombre} no vacío y un {@code estado} no nulo; la fecha límite,
 * cuando se informa, no puede ser anterior a la fecha actual.
 */
public class ProyectoController implements CrudValidator {
    private final ProyectoService proyectoService;

    /**
     * Crea un controlador de proyectos.
     *
     * @param proyectoService el servicio de proyectos a utilizar; no puede ser {@code null}
     * @throws ValidationException si {@code proyectoService} es {@code null}
     */
    public ProyectoController(ProyectoService proyectoService) {
        requireNonNull(proyectoService, "proyectoService");
        this.proyectoService = proyectoService;
    }

    /**
     * Crea un proyecto nuevo.
     *
     * @param proyecto el proyecto a crear
     * @return el proyecto creado devuelto por el servicio
     * @throws ValidationException si el proyecto es nulo, si el nombre está vacío, si el estado
     *                             es nulo, o si la fecha límite es anterior a la fecha actual
     */
    public Proyecto crear(Proyecto proyecto) {
        validateProyecto(proyecto, false);
        return proyectoService.crear(proyecto);
    }

    /**
     * Modifica un proyecto existente.
     *
     * @param proyecto el proyecto con los datos actualizados
     * @return el proyecto modificado devuelto por el servicio
     * @throws ValidationException si el proyecto es nulo, si su identificador es nulo, si el
     *                             nombre está vacío, si el estado es nulo, o si la fecha límite
     *                             es anterior a la fecha actual
     */
    public Proyecto modificar(Proyecto proyecto) {
        validateProyecto(proyecto, true);
        return proyectoService.modificar(proyecto);
    }

    /**
     * Elimina un proyecto por su identificador.
     *
     * @param id el identificador del proyecto a eliminar
     * @throws ValidationException si {@code id} es {@code null}
     */
    public void eliminar(Long id) {
        validateId(id);
        proyectoService.eliminar(id);
    }

    /**
     * Obtiene un proyecto por su identificador.
     *
     * @param id el identificador del proyecto
     * @return el proyecto encontrado
     * @throws ValidationException si {@code id} es {@code null}
     */
    public Proyecto obtenerPorId(Long id) {
        validateId(id);
        return proyectoService.obtenerPorId(id);
    }

    /**
     * Lista todos los proyectos.
     *
     * @return la lista de proyectos devuelta por el servicio
     */
    public List<Proyecto> listar() {
        return proyectoService.listar();
    }

    /**
     * Cambia el estado de un proyecto.
     *
     * @param id el identificador del proyecto
     * @param estado el nuevo estado del proyecto
     * @throws ValidationException si {@code id} es {@code null} o si {@code estado} es {@code null}
     */
    public void cambiarEstado(Long id, EstadoProyecto estado) {
        validateId(id);
        requireNonNull(estado, "estado");
        proyectoService.cambiarEstado(id, estado);
    }

    /**
     * Establece la fecha límite de un proyecto.
     *
     * @param id el identificador del proyecto
     * @param fechaLimite la fecha límite a establecer
     * @throws ValidationException si {@code id} es {@code null}, si {@code fechaLimite} es
     *                             {@code null}, o si es anterior a la fecha actual
     */
    public void establecerFechaLimite(Long id, LocalDate fechaLimite) {
        validateId(id);
        validateFechaLimite(fechaLimite);
        proyectoService.establecerFechaLimite(id, fechaLimite);
    }

    /**
     * Valida un proyecto antes de enviarlo al servicio.
     *
     * @param proyecto el proyecto a validar
     * @param requireId indica si debe exigirse un identificador no nulo
     * @throws ValidationException si el proyecto es nulo, si se exige identificador y este es
     *                             nulo, si el nombre está vacío, si el estado es nulo, o si la
     *                             fecha límite informada es anterior a la fecha actual
     */
    private void validateProyecto(Proyecto proyecto, boolean requireId) {
        requireNonNull(proyecto, "proyecto");
        if (requireId) {
            validateId(proyecto.getId());
        }
        requireText(proyecto.getNombre(), "nombre");
        requireNonNull(proyecto.getEstado(), "estado");
        if (proyecto.getFechaLimite() != null) {
            validateFechaLimite(proyecto.getFechaLimite());
        }
    }

    /**
     * Valida una fecha límite.
     *
     * @param fechaLimite la fecha a validar
     * @throws ValidationException si {@code fechaLimite} es {@code null} o es anterior a la
     *                             fecha actual
     */
    private void validateFechaLimite(LocalDate fechaLimite) {
        requireNonNull(fechaLimite, "fechaLimite");
        if (fechaLimite.isBefore(LocalDate.now())) {
            throw new ValidationException("fechaLimite no puede ser pasada");
        }
    }
}
