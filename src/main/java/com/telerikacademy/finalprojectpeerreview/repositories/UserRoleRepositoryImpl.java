package com.telerikacademy.finalprojectpeerreview.repositories;

import com.telerikacademy.finalprojectpeerreview.models.UserRole;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRoleRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleRepositoryImpl extends CRUDRepositoryImpl<UserRole> implements UserRoleRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRoleRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected Class<UserRole> getEntityClass() {
        return UserRole.class;
    }
}
