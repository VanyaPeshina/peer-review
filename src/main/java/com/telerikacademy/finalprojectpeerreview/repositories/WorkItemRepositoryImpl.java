package com.telerikacademy.finalprojectpeerreview.repositories;

import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.WorkItemRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WorkItemRepositoryImpl extends CRUDRepositoryImpl<WorkItem> implements WorkItemRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public WorkItemRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected Class<WorkItem> getEntityClass() {
        return WorkItem.class;
    }

}
