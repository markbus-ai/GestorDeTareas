package com.trullo.controller;

import com.trullo.model.EstadoProyecto;
import com.trullo.model.Proyecto;
import com.trullo.service.ProyectoService;
import java.time.LocalDate;
import java.util.List;

public class ProyectoController {
    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        if (proyectoService == null) {
            throw new IllegalArgumentException("proyectoService no puede ser nulo");
        }
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
        if (estado == null) {
            throw new IllegalArgumentException("estado no puede ser nulo");
        }
        proyectoService.cambiarEstado(id, estado);
    }

    public void establecerFechaLimite(Long id, LocalDate fechaLimite) {
        validateId(id);
        validateFechaLimite(fechaLimite);
        proyectoService.establecerFechaLimite(id, fechaLimite);
    }

    private void validateProyecto(Proyecto proyecto, boolean requireId) {
        if (proyecto == null) {
            throw new IllegalArgumentException("proyecto no puede ser nulo");
        }
        if (requireId) {
            validateId(proyecto.getId());
        }
        if (proyecto.getName() == null || proyecto.getName().isBlank()) {
            throw new IllegalArgumentException("nombre no puede ser nulo ni vacio");
        }
        if (proyecto.getStatus() == null) {
            throw new IllegalArgumentException("estado no puede ser nulo");
        }
        if (proyecto.getDueDate() != null) {
            validateFechaLimite(proyecto.getDueDate());
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id no puede ser nulo");
        }
    }

    private void validateFechaLimite(LocalDate fechaLimite) {
        if (fechaLimite == null) {
            throw new IllegalArgumentException("fechaLimite no puede ser nula");
        }
        if (fechaLimite.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("fechaLimite no puede ser pasada");
        }
    }
}
