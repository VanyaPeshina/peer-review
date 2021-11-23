package com.telerikacademy.finalprojectpeerreview.services.contracts;

import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;

import java.util.List;

public interface TeamService extends CRUDService<Team> {

    List<User> getMembers(Team team, User user);

}
