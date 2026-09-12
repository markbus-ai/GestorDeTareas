package com.trullo.service;

import com.trullo.model.EstadoProyecto;
import com.trullo.model.Proyecto;
import java.time.LocalDate;
import java.util.List;

public interface ProyectoService {
    Proyecto crear(Proyecto proyecto);

    Proyecto modificar(Proyecto proyecto);

    void eliminar(Long id);

    Proyecto obtenerPorId(Long id);

    List<Proyecto> listar();

    void cambiarEstado(Long id, EstadoProyecto estado);

    void establecerFechaLimite(Long id, LocalDate fechaLimite);
}
