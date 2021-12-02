package com.telerikacademy.finalprojectpeerreview.services.contracts;

import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.User;

import java.util.List;

public interface CRUDService<E> {

    List<E> getAll() throws EntityNotFoundException;

    E getById(int id) throws EntityNotFoundException;

    void create(E entity, User user);

    void update(E entity, User user);

    void delete(int id, User user) throws EntityNotFoundException;

    <V> E getByField(String name, V value) throws EntityNotFoundException;
}

