package com.telerikacademy.finalprojectpeerreview.repositories;

import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.CRUDRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

import static java.lang.String.format;

public abstract class CRUDRepositoryImpl<E> implements CRUDRepository<E> {

    private final SessionFactory sessionFactory;

    public CRUDRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<E> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<E> query = session.createQuery(" from " + getEntityClass().getName(), getEntityClass());
            if (query.list().size() == 0) {
                throw new EntityNotFoundException(getEntityClass().getName(), "this", "name");
            }
            return query.list();
        }
    }

    @Override
    public E getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            E entity = session.get(getEntityClass(), id);
            if (entity == null) {
                throw new EntityNotFoundException(getEntityClass().getName(), id);
            }
            return entity;
        }
    }

    @Override
    public void create(E entity) {
        try (Session session = sessionFactory.openSession()) {
            session.save(entity);
        }
    }

    @Override
    public void update(E entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(entity);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(E entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(entity);
            session.getTransaction().commit();
        }
    }

    @Override
    public <V> E getByField(String name, V value) {
        final String query = format("from %s where %s = :value", getEntityClass().getSimpleName(), name);

        try (Session session = sessionFactory.openSession()) {
            return session
                    .createQuery(query, getEntityClass())
                    .setParameter("value", value)
                    .uniqueResultOptional()
                    .orElseThrow(() -> new EntityNotFoundException(
                            getEntityClass().getSimpleName(), name, value.toString()));
        }
    }

    protected abstract Class<E> getEntityClass();
}
