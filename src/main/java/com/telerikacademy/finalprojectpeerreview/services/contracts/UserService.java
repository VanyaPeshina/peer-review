package com.telerikacademy.finalprojectpeerreview.services.contracts;

import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;

import java.util.List;
import java.util.Optional;

public interface UserService extends CRUDService<User> {

    List<User> search(Optional<String> search) throws EntityNotFoundException;

    List<WorkItem> getAllRequests(int id);
}
