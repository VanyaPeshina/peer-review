package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.CRUDRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.InvitationRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvitationServiceImpl extends CRUDServiceImpl<Invitation> implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;

    @Autowired
    public InvitationServiceImpl(CRUDRepository<Invitation> crudRepository,
                                 InvitationRepository invitationRepository,
                                 UserRepository userRepository) {
        super(crudRepository);
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void update(Invitation invitation, User user) throws DuplicateEntityException {
        if (user.getTeam() != null && user.getTeam().getName().equals(invitation.getTeam().getName())) {
            throw new DuplicateEntityException("user", "this", "team");
        }
        user.setTeam(invitation.getTeam()); userRepository.update(user);
        invitation.setDelete(1); invitationRepository.update(invitation);
    }

    @Override
    public void delete(int id, User user) throws EntityNotFoundException {
        Invitation invitationToDelete = invitationRepository.getById(id);
        invitationToDelete.setDelete(1);
        invitationRepository.update(invitationToDelete);
    }
}
