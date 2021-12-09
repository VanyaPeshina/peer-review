package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.TeamRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl extends CRUDServiceImpl<Team> implements TeamService {

    private final TeamRepository teamRepository;
    private final WorkItemService workItemService;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, WorkItemService workItemService) {
        super(teamRepository);
        this.teamRepository = teamRepository;
        this.workItemService = workItemService;
    }

    @Override
    public List<User> getMembers(Team team, User user) {
        return teamRepository.getMembers(team);
    }

    @Override
    public List<WorkItem> getTeamWorkItems(Team team) {
        return teamRepository.getTeamWorkItems(team);
    }

    @Override
    public void create(Team team, User user) {
        if (checkForDuplicates(team)) {
            throw new DuplicateEntityException(team.getClass().getSimpleName(), "these", "parameters");
        }
        checkOwner(user);
        teamRepository.create(team);
    }

    @Override
    public void delete(int id, User user) throws EntityNotFoundException {
        Team teamToDelete = teamRepository.getById(id);
        teamToDelete.setDelete(1);
        teamRepository.update(teamToDelete);
    }

    private void checkOwner(User user) {

        List<WorkItem> createdItems = workItemService.getAll()
                .stream().filter(workItem -> workItem.getCreator().getId() == user.getId())
                .collect(Collectors.toList());

        List<WorkItem> itemsForReview = workItemService.getAll()
                .stream().filter(workItem -> workItem.getReviewer().getId() == user.getId())
                .collect(Collectors.toList());

        if(createdItems.stream().anyMatch(workItem -> workItem.getStatus().getStatus().equals("Pending"))
                || createdItems.stream().anyMatch(workItem -> workItem.getStatus().getStatus().equals("Under Review"))
                || createdItems.stream().anyMatch(workItem -> workItem.getStatus().getStatus().equals("Change Requested"))) {
            throw new IllegalArgumentException("User has unfinished created work items");
        }
        if(itemsForReview.stream().anyMatch(workItem -> workItem.getStatus().getStatus().equals("Pending"))
                || itemsForReview.stream().anyMatch(workItem -> workItem.getStatus().getStatus().equals("Under Review"))
                || itemsForReview.stream().anyMatch(workItem -> workItem.getStatus().getStatus().equals("Change Requested"))) {
            throw new IllegalArgumentException("User still has work items for review");
        }
    }
}
