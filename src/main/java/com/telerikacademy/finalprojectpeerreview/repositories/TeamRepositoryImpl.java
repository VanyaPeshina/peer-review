package com.telerikacademy.finalprojectpeerreview.repositories;

import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.TeamRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeamRepositoryImpl extends CRUDRepositoryImpl<Team> implements TeamRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TeamRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    //TODO left join team
    @Override
    public List<User> getMembers(Team team) {
        try(Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where team = :team", User.class);
            query.setParameter("team", team);
            return query.list();
        }
    }
    @Override
    public List<WorkItem> getTeamWorkItems(Team team) {
        try(Session session = sessionFactory.openSession()) {
            Query<WorkItem> query = session.createQuery("from WorkItem where team = :team", WorkItem.class);
            query.setParameter("team", team);
            return query.list();
        }
    }

    @Override
    protected Class<Team> getEntityClass() {
        return Team.class;
    }
}
