package com.telerikacademy.finalprojectpeerreview.utils;

import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.services.contracts.InvitationService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserHelper {

    private final InvitationService invitationService;
    private final UserService userService;
    private final TeamService teamService;

    @Autowired
    public UserHelper(InvitationService invitationService, UserService userService, TeamService teamService) {
        this.invitationService = invitationService;
        this.userService = userService;
        this.teamService = teamService;
    }

    public List<Invitation> getUserInvitations(User user) {
        List<Invitation> invitations = invitationService.getAll();
        if (invitations.isEmpty()) {
            return invitations;
        }
        return invitations
                .stream()
                .filter(invitation -> invitation.getDelete() == 0)
                .filter(invitation -> invitation.getCreator().getId() == user.getId())
                .collect(Collectors.toList());
    }

    public List<User> getUsersToInvite(User user) {
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            return users;
        }
        return users
                .stream()
                .filter(user1 -> (user1.getTeam() == null) || (user1.getTeam().getId() != user.getTeam().getId()))
                .filter(user1 -> user1.getId() != user.getId())
                .collect(Collectors.toList());
    }

    public List<User> getTeamMembersWhenTeamIsUnknown(User user) {
        if (user.getTeam() != null) {
            return userService.getAll()
                    .stream()
                    .filter(user1 -> user1.getDelete() == 0)
                    .filter(user1 -> user1.getTeam() != null)
                    .filter(user1 -> user1.getTeam().getId() == user.getTeam().getId())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<User> getTeamMembersWithoutTheUser(User user) {
        return  teamService.getMembers(user.getTeam(), user)
                .stream().filter(user1 -> !user1.getUsername().equals(user.getUsername())).collect(Collectors.toList());
    }

    public List<Invitation> invitationsForYou(User user) {
        List<Invitation> invitations = invitationService.getAll();
        if (invitations.isEmpty()) {
            return invitations;
        }
        return invitations
                .stream()
                .filter(invitation -> invitation.getDelete() == 0)
                .filter(invitation -> invitation.getInvited().getId() == user.getId())
                .collect(Collectors.toList());
    }
}
