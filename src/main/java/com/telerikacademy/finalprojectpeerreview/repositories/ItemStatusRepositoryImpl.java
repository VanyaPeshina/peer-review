package com.telerikacademy.finalprojectpeerreview.repositories;

import com.telerikacademy.finalprojectpeerreview.models.ItemStatus;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.ItemStatusRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ItemStatusRepositoryImpl extends CRUDRepositoryImpl<ItemStatus> implements ItemStatusRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public ItemStatusRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected Class<ItemStatus> getEntityClass() {
        return ItemStatus.class;
    }
}
