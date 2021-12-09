package com.telerikacademy.finalprojectpeerreview.repositories;

import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.InvitationRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InvitationRepositoryImpl extends CRUDRepositoryImpl<Invitation> implements InvitationRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public InvitationRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected Class<Invitation> getEntityClass() {
        return Invitation.class;
    }
}
