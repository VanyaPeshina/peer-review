package com.telerikacademy.finalprojectpeerreview.controllers.mvc;


import com.telerikacademy.finalprojectpeerreview.exceptions.AuthenticationFailureException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.services.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.InvitationService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final FileStorageService fileStorageService;
    private final WorkItemService workItemService;
    private final TeamService teamService;
    private final InvitationService invitationService;

    public DashboardMvcController(UserService userService, AuthenticationHelper authenticationHelper,
                                  FileStorageService fileStorageService, WorkItemService workItemService, TeamService teamService, InvitationService invitationService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.fileStorageService = fileStorageService;
        this.workItemService = workItemService;
        this.teamService = teamService;
        this.invitationService = invitationService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("SPRING_SECURITY_CONTEXT") != null;
    }

    @GetMapping
    public String showDashboardPage(Model model, HttpSession session, HttpServletRequest request,
                                    Principal user1) {
        User user = null;
        try {
            user = userService.getByField("username", user1.getName());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
      /*  try {
            user = userService.getByField("username", user1.getName());*/
            //user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //user = authenticationHelper.tryGetUser(session);
     /*   } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }*/
        model.addAttribute("user", user);
        List<WorkItem> workItemsCreatedByUser = getWorkItemsCreatedByUser(user);
        if(user.getTeam() != null){
            List<WorkItem> teamItems = getTeamsItems(user);
            model.addAttribute("teamItems", teamItems);
        }
        List<WorkItem> workItemsForReview = getWorkItemsForReview(user);
        List<WorkItem> itemsNeedingChange = getItemsNeedingChange(user);

        model.addAttribute("workItems", workItemsCreatedByUser);
        model.addAttribute("waitingForReview", workItemsForReview);
        model.addAttribute("needingChange", itemsNeedingChange);
        model.addAttribute("members", teamService.getMembers(user.getTeam(), user));
        model.addAttribute("invitations", getUserInvitations(user));
        model.addAttribute("today", LocalDateTime.now());
        model.addAttribute("photo", "/api/users/" + user.getId() + "/photo");
        return "dashboard";
    }

    private List<WorkItem> getItemsNeedingChange(User user) {
        if (workItemService.getAll().size() == 0) {
            return workItemService.getAll();
        }
        return userService.getAllRequests(user.getId())
                .stream()
                .filter(workItem -> workItem.getStatus().getStatus().equals("Change Requested"))
                .collect(Collectors.toList());
    }

    private List<WorkItem> getWorkItemsForReview(User user) {
        if (workItemService.getAll().size() == 0) {
            return workItemService.getAll();
        }
        return userService.getAllRequests(user.getId())
                .stream()
                .filter(workItem -> workItem.getReviewer() != null)
                .filter(workItem -> workItem.getReviewer().getId() == user.getId())
                .collect(Collectors.toList());
    }

    private List<WorkItem> getTeamsItems(User user) {
        if (workItemService.getAll().size() == 0) {
            return workItemService.getAll();
        }
        return workItemService.getAll()
                .stream()
                .filter(workItem -> workItem.getTeam().getId() == user.getTeam().getId())
                .collect(Collectors.toList());
    }

    private List<WorkItem> getWorkItemsCreatedByUser(User user) {
        if (workItemService.getAll().size() == 0) {
            return workItemService.getAll();
        }
        return workItemService.getAll()
                .stream()
                .filter(workItem -> workItem.getCreator().getId() == user.getId())
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
