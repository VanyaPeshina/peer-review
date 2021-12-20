package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.InvitationDTO;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.mappers.InvitationMapper;
import com.telerikacademy.finalprojectpeerreview.services.contracts.InvitationService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.utils.UserHelper;
import com.telerikacademy.finalprojectpeerreview.utils.WorkItemsHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/invitation")
public class InvitationController {

    private final UserService userService;
    private final InvitationMapper invitationMapper;
    private final InvitationService invitationService;
    private final UserHelper userHelper;
    private final WorkItemsHelper workItemsHelper;

    public InvitationController(UserService userService,
                                InvitationMapper invitationMapper,
                                InvitationService invitationService,
                                UserHelper userHelper,
                                WorkItemsHelper workItemsHelper) {
        this.userService = userService;
        this.invitationService = invitationService;
        this.invitationMapper = invitationMapper;
        this.userHelper = userHelper;
        this.workItemsHelper = workItemsHelper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("SPRING_SECURITY_CONTEXT") != null;
    }

    @ModelAttribute("isAdmin")
    public boolean checkForAdmin(Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        return user.getRole().getRole().equals("Admin");
    }

    @ModelAttribute("invitationsForYou")
    public List<Invitation> populateIs(Principal principal) {
        return userHelper.invitationsForYou((User) userService.loadUserByUsername(principal.getName()));
    }

    @GetMapping("/all")
    public String getSentInvitations(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("sentInvitations", userHelper.getUserInvitations(user));

        return "invitations";
    }

    @GetMapping
    private String newInvitation(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("invitationDto", new InvitationDTO());
        model.addAttribute("users", userHelper.getUsersToInvite(user));

        return "create_invitation";
    }

    @PostMapping
    public String createInvitation(@Valid @ModelAttribute("invitationDto") InvitationDTO invitationDTO,
                                   BindingResult errors,
                                   Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if (errors.hasErrors()) {
            return "dashboard";
        }
        try {
            invitationDTO.setCreatorId(user.getId());
            Invitation invitation = invitationMapper.fromDto(invitationDTO);
            invitationService.create(invitation, user);
            return "redirect:/invitation/all";
        } catch (DuplicateEntityException e) {
            return "create_team";
        } catch (EntityNotFoundException e) {
            return "error-404";
        }
    }

    @GetMapping("/accept/{id}")
    public String singleAcceptInvitationPage(@PathVariable int id,
                                             Model model,
                                             Principal principal) {
        User user;
        try {
            user = userService.getByField("username", principal.getName());
        } catch (EntityNotFoundException e) {
            return "redirect:/login";
        }
        try {
            Invitation invitation = invitationService.getById(id);
            InvitationDTO invitationDTO = invitationMapper.toDto(invitation);
            model.addAttribute("user", user);
            model.addAttribute("invitationDTO", invitationDTO);
            model.addAttribute("userInvitation", invitation.getCreator().getUsername());
            model.addAttribute("teamInvitation", invitation.getTeam().getName());
            return "single_invitation";
        } catch (EntityNotFoundException e) {
            return "error-404";
        }
    }

    @PostMapping("/accept/{id}")
    private String acceptInvitation(@PathVariable int id,
                                    @Valid @ModelAttribute("invitationDTO") InvitationDTO dto,
                                    Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        if (user.getTeam() != null) {
            if (workItemsHelper.checkForUnfinishedWorkItems(user)) {
                return "error_leave_team";
            }
        }
        try {
            Invitation invitation = invitationService.getById(id);
            invitationService.update(invitation, user);
            return "redirect:/my_dashboard";
        } catch (EntityNotFoundException e) {
            return "error-404";
        } catch (DuplicateEntityException | UnauthorizedOperationException e) {
            //TODO
            e.printStackTrace();
            return "single_invitation";
        }
    }

    @GetMapping("/accept/{id}/delete")
    public String deleteInvitation(@PathVariable int id, Model model, Principal principal) {
        User userToAuthenticate = (User) userService.loadUserByUsername(principal.getName());
        try {
            invitationService.delete(id, userToAuthenticate);
            return "redirect:/invitation/all";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-404";
        }
    }
}
