package com.trullo.services;

import com.trullo.exception.NotFoundException;
import com.trullo.interfaces.dao.ProjectDAOInterface;
import com.trullo.interfaces.services.ProyectoService;
import com.trullo.model.EstadoProyecto;
import com.trullo.model.Proyecto;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de {@link ProyectoService} que delega la persistencia en
 * {@link ProjectDAOInterface}.
 */
public class ProyectoServiceImpl implements ProyectoService {
    private static final String ENTIDAD = "Proyecto";

    private final ProjectDAOInterface projectDAO;

    /**
     * Crea el servicio de proyectos.
     *
     * @param projectDAO el DAO de proyectos a utilizar
     */
    public ProyectoServiceImpl(ProjectDAOInterface projectDAO) {
        this.projectDAO = projectDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Proyecto crear(Proyecto proyecto) {
        return projectDAO.create(proyecto);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el proyecto no existe
     */
    @Override
    public Proyecto modificar(Proyecto proyecto) {
        obtenerPorId(proyecto.getId());
        return projectDAO.update(proyecto);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el proyecto no existe
     */
    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        projectDAO.delete(id);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el proyecto no existe
     */
    @Override
    public Proyecto obtenerPorId(Long id) {
        return NotFoundException.requireFound(projectDAO.findById(id), ENTIDAD, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Proyecto> listar() {
        return projectDAO.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el proyecto no existe
     */
    @Override
    public void cambiarEstado(Long id, EstadoProyecto estado) {
        Proyecto proyecto = obtenerPorId(id);
        proyecto.setStatus(estado);
        projectDAO.update(proyecto);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el proyecto no existe
     */
    @Override
    public void establecerFechaLimite(Long id, LocalDate fechaLimite) {
        Proyecto proyecto = obtenerPorId(id);
        proyecto.setDueDate(fechaLimite);
        projectDAO.update(proyecto);
    }
}
