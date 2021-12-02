package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.AuthenticationFailureException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/team")
public class TeamMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final TeamService teamService;

    public TeamMvcController(AuthenticationHelper authenticationHelper, UserService userService, TeamService teamService) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.teamService = teamService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String showTeamPage(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }
        try {
            model.addAttribute("user", user);
            Team team = teamService.getById(user.getTeam().getId());

            List<User> teamMembers = teamService.getMembers(team, user);
            model.addAttribute("teamMembers", teamMembers);
            List<WorkItem> workItems = teamService.getTeamWorkItems(team);
            model.addAttribute("workItems", workItems);
            /*model.addAttribute("photo", "/api/users/" + user.getId() + "/photo");*/
            return "team";
        } catch (EntityNotFoundException | NullPointerException e) {
            return "team";
        }
    }
}
