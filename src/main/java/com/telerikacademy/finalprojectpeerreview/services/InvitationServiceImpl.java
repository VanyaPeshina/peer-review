package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.CRUDRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.InvitationRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.WorkItemRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class InvitationServiceImpl extends CRUDServiceImpl<Invitation> implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final WorkItemRepository workItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public InvitationServiceImpl(CRUDRepository<Invitation> crudRepository, InvitationRepository invitationRepository,
                                 WorkItemRepository workItemRepository, UserRepository userRepository) {
        super(crudRepository);
        this.invitationRepository = invitationRepository;
        this.workItemRepository = workItemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void update(Invitation invitation, User user) {
        if (user.getTeam() != null && user.getTeam().getName().equals(invitation.getTeam().getName())) {
            throw new DuplicateEntityException("user", "this", "team");
        }
        checkForUnfinishedWorkItems(user);
        user.setTeam(invitation.getTeam());
        userRepository.update(user);
        invitationRepository.delete(invitation);
    }

    private void checkForUnfinishedWorkItems(User user) {
        List<WorkItem> itemsToCheck = workItemRepository.getAll()
                .stream()
                .filter(workItem -> !workItem.getStatus().getStatus().equals("Accepted")
                        || !workItem.getStatus().getStatus().equals("Rejected"))
                .collect(Collectors.toList());

        if (itemsToCheck.stream()
                .anyMatch(workItem -> workItem.getCreator().getUsername().equals(user.getUsername()) ||
                        workItem.getReviewer().getUsername().equals(user.getUsername()))) {
            throw new IllegalArgumentException("User has unfinished items and can't go in a new team");
        }
    }
}
