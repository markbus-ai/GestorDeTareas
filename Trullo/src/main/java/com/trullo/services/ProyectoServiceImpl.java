package com.trullo.services;

import com.trullo.exception.DaoException;
import com.trullo.exception.NotFoundException;
import com.trullo.exception.ServiceException;
import com.trullo.interfaces.dao.ProjectDAOInterface;
import com.trullo.interfaces.services.ProyectoService;
import com.trullo.model.EstadoProyecto;
import com.trullo.model.Proyecto;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de {@link ProyectoService} que delega la persistencia en
 * {@link ProjectDAOInterface}.
 *
 * <p>Los errores de persistencia se traducen a {@link ServiceException} con un mensaje claro.
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
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Proyecto crear(Proyecto proyecto) {
        try {
            return projectDAO.create(proyecto);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo crear el proyecto", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el proyecto no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Proyecto modificar(Proyecto proyecto) {
        obtenerPorId(proyecto.getId());
        try {
            return projectDAO.update(proyecto);
        } catch (DaoException e) {
            throw new ServiceException(
                    "No se pudo modificar el proyecto con id " + proyecto.getId(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el proyecto no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        try {
            projectDAO.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo eliminar el proyecto con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el proyecto no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public Proyecto obtenerPorId(Long id) {
        try {
            return NotFoundException.requireFound(projectDAO.findById(id), ENTIDAD, id);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo obtener el proyecto con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public List<Proyecto> listar() {
        try {
            return projectDAO.findAll();
        } catch (DaoException e) {
            throw new ServiceException("No se pudieron listar los proyectos", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el proyecto no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void cambiarEstado(Long id, EstadoProyecto estado) {
        Proyecto proyecto = obtenerPorId(id);
        proyecto.setStatus(estado);
        try {
            projectDAO.update(proyecto);
        } catch (DaoException e) {
            throw new ServiceException("No se pudo cambiar el estado del proyecto con id " + id, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotFoundException si el proyecto no existe
     * @throws ServiceException si falla la persistencia
     */
    @Override
    public void establecerFechaLimite(Long id, LocalDate fechaLimite) {
        Proyecto proyecto = obtenerPorId(id);
        proyecto.setDueDate(fechaLimite);
        try {
            projectDAO.update(proyecto);
        } catch (DaoException e) {
            throw new ServiceException(
                    "No se pudo establecer la fecha límite del proyecto con id " + id, e);
        }
    }
}
