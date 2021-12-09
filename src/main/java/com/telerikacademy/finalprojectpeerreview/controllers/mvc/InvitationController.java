package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.AuthenticationFailureException;
import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.InvitationDTO;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.mappers.InvitationMapper;
import com.telerikacademy.finalprojectpeerreview.services.contracts.InvitationService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/invitation")
public class InvitationController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final InvitationMapper invitationMapper;
    private final InvitationService invitationService;

    public InvitationController(UserService userService, AuthenticationHelper authenticationHelper,
                                InvitationMapper invitationMapper, InvitationService invitationService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.invitationService = invitationService;
        this.invitationMapper = invitationMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/all")
    public String workItemForReview(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("invitations", getUserInvitations(user));
        return "invitations";
    }

    @GetMapping
    private String newInvitation(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("invitationDto", new InvitationDTO());
        model.addAttribute("users", getUsers(user));
        return "create_invitation";
    }

    @PostMapping
    public String createInvitation(@Valid @ModelAttribute("invitationDto") InvitationDTO invitationDTO,
                             BindingResult errors, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        if (errors.hasErrors()) {
            return "dashboard";
        }
        try {
            invitationDTO.setCreatorId(user.getId());
            Invitation invitation = invitationMapper.fromDto(invitationDTO);
            invitationService.create(invitation, user);
            //TODO redirect:/invitations_sent
            return "redirect:/dashboard";
        } catch (DuplicateEntityException e) {
            return "create_team";
        } catch (UnauthorizedOperationException e) {
            return "forbidden_403";
        } catch (EntityNotFoundException e) {
            return "error-404";
        }
    }

    @PutMapping("/all/{id}")
    private String acceptInvitation(@PathVariable int id, BindingResult errors, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        if (errors.hasErrors()) {
            return "redirect:/invitation/all";
        }
        try {
            Invitation invitation = invitationService.getById(id);
            invitationService.update(invitation, user);
            return "redirect:/invitation/all";
        } catch (EntityNotFoundException e) {
            return "error-404";
        }
    }

    private List<User> getUsers(User user) {
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            return users;
        }
        return users
                .stream()
                .filter(user1 -> user1.getTeam().getId() != user.getTeam().getId())
                .filter(user1 -> user1.getId() != user.getId())
                .collect(Collectors.toList());
    }

    public List<Invitation> getUserInvitations(User user) {
        List<Invitation> invitations = invitationService.getAll();
        if (invitations.isEmpty()) {
            return invitations;
        }
        return invitations
                .stream()
                .filter(invitation -> invitation.getInvited().getId() == user.getId())
                .collect(Collectors.toList());
    }
}
