package com.trullo.controller;

import com.trullo.interfaces.BaseInterface;
import com.trullo.exception.ValidationException;
import com.trullo.interfaces.services.ProyectoService;
import com.trullo.model.EstadoProyecto;
import com.trullo.model.Proyecto;

import java.time.LocalDate;
import java.util.List;

public class ProyectoController implements CrudValidator, BaseInterface<Proyecto> {
    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        requireNonNull(proyectoService, "proyectoService");
        this.proyectoService = proyectoService;
    }

    @Override
    public Proyecto create(Proyecto proyecto) {
        validateProyecto(proyecto, false);
        return proyectoService.crear(proyecto);
    }

    @Override
    public Proyecto update(Proyecto proyecto) {
        validateProyecto(proyecto, true);
        return proyectoService.modificar(proyecto);
    }

    @Override
    public void delete(Long id) {
        validateId(id);
        proyectoService.eliminar(id);
    }

    @Override
    public Proyecto findById(Long id) {
        validateId(id);
        return proyectoService.obtenerPorId(id);
    }

    @Override
    public List<Proyecto> findAll() {
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
        requireText(proyecto.getName(), "nombre");
        requireNonNull(proyecto.getStatus(), "estado");
        if (proyecto.getDueDate() != null) {
            validateFechaLimite(proyecto.getDueDate());
        }
    }

    private void validateFechaLimite(LocalDate fechaLimite) {
        requireNonNull(fechaLimite, "fechaLimite");
        if (fechaLimite.isBefore(LocalDate.now())) {
            throw new ValidationException("fechaLimite no puede ser pasada");
        }
    }
}
