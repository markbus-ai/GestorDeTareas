package com.trullo.controller;

import com.trullo.exception.ValidationException;
import com.trullo.model.EstadoProyecto;
import com.trullo.model.Proyecto;
import com.trullo.service.ProyectoService;
import java.time.LocalDate;
import java.util.List;

public class ProyectoController implements CrudValidator {
    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        requireNonNull(proyectoService, "proyectoService");
        this.proyectoService = proyectoService;
    }

    public Proyecto crear(Proyecto proyecto) {
        validateProyecto(proyecto, false);
        return proyectoService.crear(proyecto);
    }

    public Proyecto modificar(Proyecto proyecto) {
        validateProyecto(proyecto, true);
        return proyectoService.modificar(proyecto);
    }

    public void eliminar(Long id) {
        validateId(id);
        proyectoService.eliminar(id);
    }

    public Proyecto obtenerPorId(Long id) {
        validateId(id);
        return proyectoService.obtenerPorId(id);
    }

    public List<Proyecto> listar() {
        return proyectoService.listar();
    }

    public void cambiarEstado(Long id, EstadoProyecto estado) {
        validateId(id);
        requireNonNull(estado, "estado");
        proyectoService.cambiarEstado(id, estado);
    }

    public void establecerFechaLimite(Long id, LocalDate fechaLimite) {
        validateId(id);
        validateFechaLimite(fechaLimite);
        proyectoService.establecerFechaLimite(id, fechaLimite);
    }

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

    private void validateFechaLimite(LocalDate fechaLimite) {
        requireNonNull(fechaLimite, "fechaLimite");
        if (fechaLimite.isBefore(LocalDate.now())) {
            throw new ValidationException("fechaLimite no puede ser pasada");
        }
    }
}
