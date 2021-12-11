package com.telerikacademy.finalprojectpeerreview.repositories;

import com.telerikacademy.finalprojectpeerreview.repositories.contracts.ConfirmationTokenRepository;
import com.telerikacademy.finalprojectpeerreview.models.ConfirmationToken;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ConfirmationTokenRepositoryImpl extends CRUDRepositoryImpl<ConfirmationToken> implements ConfirmationTokenRepository {

    @Autowired
    public ConfirmationTokenRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected Class<ConfirmationToken> getEntityClass() {
        return ConfirmationToken.class;
    }

}
