package com.telerikacademy.finalprojectpeerreview.repositories;

import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl extends CRUDRepositoryImpl<User> implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

  /*  @Override
    public List<User> getAll() {
        try (Session session = sessionFactory.openSession()) {
            String queryString = " from User where delete = 0";
            Query<User> query = session.createQuery(queryString, User.class);
            return query.list();
        }
    }*/

    //LEFT JOIN NEEDED BECAUSE OF THE POSSIBLE NULL VALUES FOR TEAM IN THE DB
    @Override
    public List<User> search(String search) {
        try (Session session = sessionFactory.openSession()) {
            String queryString = "select distinct u " +
                    "from User u " +
                    "left join u.team " +
                    "where u.username like :search " +
                    "or u.email like :search " +
                    "or u.phone like :search";
            Query<User> query = session.createQuery(queryString, User.class);
            query.setParameter("search", "%" + search + "%");
            return query.list();
        }
    }

    //LEFT JOIN NEEDED BECAUSE OF THE POSSIBLE NULL VALUES FOR REVIEWER IN THE DB
    @Override
    public List<WorkItem> getAllRequests(int id) {
        try (Session session = sessionFactory.openSession()) {
            String queryString = "select distinct w from WorkItem w left join w.reviewer where w.creator.id = :id";
            Query<WorkItem> query = session.createQuery(queryString, WorkItem.class);
            query.setParameter("id", id);
            return query.list();
        }
    }

    @Override
    public Optional<User> selectApplicationUserByUsername(String username) {
        return getAll()
                .stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst();
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }
}
