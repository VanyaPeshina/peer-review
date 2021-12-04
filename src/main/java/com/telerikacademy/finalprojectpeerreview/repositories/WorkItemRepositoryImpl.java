package com.telerikacademy.finalprojectpeerreview.repositories;

import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.WorkItemRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.io.File.*;

@Repository
public class WorkItemRepositoryImpl extends CRUDRepositoryImpl<WorkItem> implements WorkItemRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public WorkItemRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<WorkItem> search(String search) {
        try (Session session = sessionFactory.openSession()) {
            String queryString = "select distinct w " +
                    "from WorkItem w " +
                    "left join w.reviewer " +
                    "where w.status.status like :search " +
                    "or w.creator.username like :search ";
            Query<WorkItem> query = session.createQuery(queryString, WorkItem.class);
            query.setParameter("search", "%" + search + "%");
            return query.list();
        }
    }

    @Override
    public List<WorkItem> filter(Optional<String> name, Optional<String> status,
                                 Optional<String> reviewer, Optional<String> sort) {
        try (Session session = sessionFactory.openSession()) {
            var queryString = new StringBuilder("from WorkItem ");
            var filters = new ArrayList<String>();
            var params = new HashMap<String, Object>();

            name.ifPresent(value -> {
                filters.add("name like :name");
                params.put("name", value);
            });

            status.ifPresent(value -> {
                filters.add("status.status like :status");
                params.put("status", value);
            });

            reviewer.ifPresent(value -> {
                filters.add("reviewer.username like :reviewer");
                params.put("reviewer", value);
            });

            if (!filters.isEmpty()) {
                queryString.append("where ").append(String.join(" and ", filters));
            }

            sort.ifPresent(value -> {
                queryString.append(generateSortingString(value));
            });

            Query<WorkItem> query = session.createQuery(queryString.toString(), WorkItem.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public List<WorkItem> filterMVC(Optional<Integer> creatorId,
                                    Optional<Integer> reviewerId,
                                    Optional<Integer> statusId) {

        try (Session session = sessionFactory.openSession()) {
            var queryString  = new StringBuilder("from WorkItem ");
            var filters = new ArrayList<String>();
            var params = new HashMap<String, Object>();

            if (creatorId.get() > 0) {
                creatorId.ifPresent(value -> {
                    filters.add("creator.id = :creatorId");
                    params.put("creatorId", value);
                });
            }
            if (reviewerId.get() > 0) {
                reviewerId.ifPresent(value -> {
                    filters.add("reviewer.id = :reviewerId");
                    params.put("reviewerId", value);
                });
            }
            if (statusId.get() > 0) {
                statusId.ifPresent(value -> {
                    filters.add("status.id = :statusId");
                    params.put("statusId", value);
                });
            }
            if (!filters.isEmpty()) {
                queryString.append("where ").append(String.join(" and ", filters));
            }

            Query<WorkItem> query = session.createQuery(queryString.toString(), WorkItem.class);
            query.setProperties(params);
            return query.list();
        }
    }

    private String generateSortingString(String value) {
        StringBuilder result = new StringBuilder(" order by ");
        var params = value.toLowerCase().split("_");

        for (String param : params) {
            if (param.equals("id")) {
                result.append("id ");
            }
            if (param.equals("title")) {
                result.append("title ");
            }
            if (param.equals("status")) {
                result.append("status.status ");
            }
            if (param.equals("desc")) {
                result.append("id desc ");
            }
        }

        return result.toString();
    }

    @Override
    protected Class<WorkItem> getEntityClass() {
        return WorkItem.class;
    }
}
