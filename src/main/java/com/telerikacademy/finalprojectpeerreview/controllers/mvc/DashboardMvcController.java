package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.utils.UserHelper;
import com.telerikacademy.finalprojectpeerreview.utils.WorkItemsHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardMvcController {

    private final UserService userService;
    private final TeamService teamService;
    private final WorkItemsHelper workItemsHelper;
    private final UserHelper userHelper;

    public DashboardMvcController(UserService userService,
                                  TeamService teamService,
                                  WorkItemsHelper workItemsHelper,
                                  UserHelper userHelper) {
        this.userService = userService;
        this.teamService = teamService;
        this.workItemsHelper = workItemsHelper;
        this.userHelper = userHelper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("SPRING_SECURITY_CONTEXT") != null;
    }

    @ModelAttribute("invitationsForYou")
    public List<Invitation> populateInvitations(Principal principal) {
        return userHelper.invitationsForYou((User) userService.loadUserByUsername(principal.getName()));
    }

    @GetMapping
    public String showDashboardPage(Model model, Principal principal) {
        User user;
        try {
           user = (User) userService.loadUserByUsername(principal.getName());
        } catch (NullPointerException e) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("workItems", workItemsHelper.getWorkItemsCreatedByUser(user));
        model.addAttribute("teamItems", workItemsHelper.getTeamsItems(user));
        model.addAttribute("waitingForReview", workItemsHelper.getWorkItemsForReview(user));
        model.addAttribute("needingChange", workItemsHelper.getItemsNeedingChange(user));
        model.addAttribute("members", teamService.getMembers(user.getTeam(), user));
        model.addAttribute("invitations", userHelper.getUserInvitations(user));
        model.addAttribute("invitationsForYou", userHelper.invitationsForYou(user));
        model.addAttribute("today", LocalDateTime.now());
        model.addAttribute("photo", "/api/users/" + user.getId() + "/photo");

        return "dashboard";
    }
}
