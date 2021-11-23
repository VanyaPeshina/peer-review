package com.telerikacademy.finalprojectpeerreview.services.contracts;

import com.telerikacademy.finalprojectpeerreview.models.User;

import java.util.List;

public interface CRUDService<E> {

    List<E> getAll();

    E getById(int id);

    void create(E entity, User user);

    void update(E entity, User user);

    void delete(int id, User user);

    <V> E getByField(String name, V value);
}

