package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.services.contracts.CRUDService;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.CRUDRepository;

import java.util.List;

public class CRUDServiceImpl<E> implements CRUDService<E> {

    private final CRUDRepository<E> crudRepository;

    public CRUDServiceImpl(CRUDRepository<E> crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Override
    public List<E> getAll() throws EntityNotFoundException {
        return crudRepository.getAll();
    }

    @Override
    public E getById(int id) throws EntityNotFoundException {
        return crudRepository.getById(id);
    }

    @Override
    public void create(E entity, User user) {
        if (checkEntityForDuplicates(entity)) {
            crudRepository.create(entity);
        }
    }

    @Override
    public void update(E entity, User user) {
        if (checkEntityForDuplicates(entity)) {
            crudRepository.update(entity);
        }
    }

    @Override
    public void delete(int id, User user) throws EntityNotFoundException {
        E entityToDelete = crudRepository.getById(id);
        crudRepository.delete(entityToDelete);
    }

    @Override
    public <V> E getByField(String name, V value) throws EntityNotFoundException {
        return crudRepository.getByField(name, value);
    }

    protected boolean checkEntityForDuplicates(E entity) {
        try {
            List<E> allEntities = crudRepository.getAll();
            for (E existingEntity : allEntities) {
                if (existingEntity.equals(entity)) {
                    throw new DuplicateEntityException(entity.getClass().getSimpleName(), "these", "parameters");
                }
            }
        } catch (EntityNotFoundException e) {
            return true;
        }
        return true;
    }
}
