package com.telerikacademy.finalprojectpeerreview.repositories.contracts;

import com.telerikacademy.finalprojectpeerreview.models.User;

import java.util.List;

public interface UserRepository extends CRUDRepository<User> {

    List<User> search(String search);

    /*User getByUsername(String username);*/
}
