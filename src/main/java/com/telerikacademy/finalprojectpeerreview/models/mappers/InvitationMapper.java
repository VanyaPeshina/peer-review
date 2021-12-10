package com.telerikacademy.finalprojectpeerreview.models.mappers;

import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.InvitationDTO;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.TeamRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InvitationMapper {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public InvitationMapper(UserRepository userRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    public Invitation fromDto(InvitationDTO invitationDTO) throws EntityNotFoundException {
        Invitation invitation = new Invitation();
        DTOtoObject(invitationDTO, invitation);
        return invitation;
    }

    private void DTOtoObject(InvitationDTO invitationDTO, Invitation invitation) throws EntityNotFoundException {
        User creator = userRepository.getById(invitationDTO.getCreatorId());
        invitation.setCreator(creator);
        invitation.setTeam(teamRepository.getById(creator.getTeam().getId()));
        invitation.setInvited(userRepository.getById(invitationDTO.getInvitedId()));
        invitation.setDate(LocalDateTime.now());
        invitation.setDelete(0);

    }

    public InvitationDTO toDto(Invitation invitation) {
        InvitationDTO invitationDTO = new InvitationDTO();
        invitationDTO.setCreatorId(invitation.getId());
        invitationDTO.setInvitedId(invitation.getInvited().getId());
        invitationDTO.setTeamId(invitation.getTeam().getId());
        return invitationDTO;
    }
}
