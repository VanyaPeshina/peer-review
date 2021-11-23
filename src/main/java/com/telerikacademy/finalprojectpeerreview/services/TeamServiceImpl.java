package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.TeamRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamServiceImpl extends CRUDServiceImpl<Team> implements TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository) {
        super(teamRepository);
        this.teamRepository = teamRepository;
    }

    @Override
    public List<User> getMembers(Team team, User user) {
        /* AuthorizationCheck.checkForEmployee(user);*/
        return teamRepository.getMembers(team);
    }
}
