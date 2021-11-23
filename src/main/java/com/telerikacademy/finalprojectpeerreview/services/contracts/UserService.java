package com.telerikacademy.finalprojectpeerreview.services.contracts;

import com.telerikacademy.finalprojectpeerreview.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends CRUDService<User> {

    List<User> search(Optional<String> search);

    /* User getByUsername(String username);*/
}
