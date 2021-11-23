package com.telerikacademy.finalprojectpeerreview.repositories.contracts;

import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;

import java.util.List;

public interface TeamRepository extends CRUDRepository<Team>{

    List<User> getMembers(Team team);
}
