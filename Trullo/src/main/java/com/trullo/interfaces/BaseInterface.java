package com.trullo.interfaces;

import java.util.List;

/**
 * Define una interfaz base con las operaciones CRUD para las entidades
 * que la implementen.
 *
 * @param <T> tipo de entidad sobre la que se realizarán las operaciones
 */
public interface BaseInterface<T> {

    /**
     * Crea una nueva entidad.
     *
     * @param entity entidad que se desea crear
     * @return entidad creada
     */
    T create(T entity);

    /**
     * Actualiza una entidad existente.
     *
     * @param entity entidad con los datos actualizados
     * @return entidad actualizada
     */
    T update(T entity);

    /**
     * Elimina una entidad por su identificador.
     *
     * @param id identificador de la entidad que se desea eliminar
     */
    void delete(Long id);
    
    /**
     * Busca una entidad por su identificador.
     *
     * @param id identificador de la entidad
     * @return entidad encontrada, o null si no existe
     */
    T findById(Long id);
    
    /**
     * Obtiene todas las entidades almacenadas.
     *
     * @return lista de entidades encontradas; vacía si no hay registros
     */
    List<T> findAll();
}
