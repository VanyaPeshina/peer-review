package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.CRUDRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.InvitationRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.InvitationService;
import com.telerikacademy.finalprojectpeerreview.utils.WorkItemsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvitationServiceImpl extends CRUDServiceImpl<Invitation> implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final WorkItemsHelper workItemsHelper;

    @Autowired
    public InvitationServiceImpl(CRUDRepository<Invitation> crudRepository,
                                 InvitationRepository invitationRepository,
                                 UserRepository userRepository,
                                 WorkItemsHelper workItemsHelper) {
        super(crudRepository);
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.workItemsHelper = workItemsHelper;
    }

    @Override
    public void update(Invitation invitation, User user) {
        if (user.getTeam() != null && user.getTeam().getName().equals(invitation.getTeam().getName())) {
            throw new DuplicateEntityException("user", "this", "team");
        }
        //workItemsHelper.checkForUnfinishedWorkItems(user);
        user.setTeam(invitation.getTeam());
        userRepository.update(user);
        invitationRepository.delete(invitation);
    }

    @Override
    public void delete(int id, User user) throws EntityNotFoundException {
        Invitation invitationToDelete = invitationRepository.getById(id);
        invitationToDelete.setDelete(1);
        invitationRepository.update(invitationToDelete);
    }
}
