package com.telerikacademy.finalprojectpeerreview.repositories;

import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.InvitationRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InvitationRepositoryImpl extends CRUDRepositoryImpl<Invitation> implements InvitationRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public InvitationRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }
/*
    @Override
    public List<Invitation> getAll() {
        try (Session session = sessionFactory.openSession()) {
            String queryString = " from Invitation where delete = 0";
            Query<Invitation> query = session.createQuery(queryString, Invitation.class);
            return query.list();
        }
    }*/

    @Override
    protected Class<Invitation> getEntityClass() {
        return Invitation.class;
    }
}
