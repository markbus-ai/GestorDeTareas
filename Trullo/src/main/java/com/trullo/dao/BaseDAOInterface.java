package com.trullo.dao;

import java.util.List;

public interface BaseDAOInterface<T> {
    T create(T entity);
    T update(T entity);
    void delete(Long id);
    T findById(Long id);
    List<T> findAll();
}
