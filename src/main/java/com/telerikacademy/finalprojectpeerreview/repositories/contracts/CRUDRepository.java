package com.telerikacademy.finalprojectpeerreview.repositories.contracts;

import java.util.List;

public interface CRUDRepository<E> {

    List<E> getAll();

    E getById(int id);

    void create(E entity);

    void update(E entity);

    void delete(E entity);

    <V> E getByField(String name, V value);

}