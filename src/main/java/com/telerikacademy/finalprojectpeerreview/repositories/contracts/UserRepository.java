package com.telerikacademy.finalprojectpeerreview.repositories.contracts;

import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;

import java.util.List;

public interface UserRepository extends CRUDRepository<User> {

    List<User> search(String search);

    List<WorkItem> getAllRequests(int id);

}
