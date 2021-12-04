package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.AuthenticationFailureException;
import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.TeamDTO;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.WorkItemDTO;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.models.mappers.TeamMapper;
import com.telerikacademy.finalprojectpeerreview.services.contracts.ItemStatusService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/team")
public class TeamMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final TeamService teamService;
    private final TeamMapper teamMapper;
    private final ItemStatusService itemStatusService;

    public TeamMvcController(AuthenticationHelper authenticationHelper, UserService userService,
                             TeamService teamService, TeamMapper teamMapper, ItemStatusService itemStatusService) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.teamService = teamService;
        this.teamMapper = teamMapper;
        this.itemStatusService = itemStatusService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/all")
    public String showTeamPage(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        try {
            model.addAttribute("user", user);
            Team team = teamService.getById(user.getTeam().getId());

            List<User> teamMembers = teamService.getMembers(team, user);
            model.addAttribute("teamMembers", teamMembers);
            List<WorkItem> workItems = teamService.getTeamWorkItems(team);
            model.addAttribute("workItems", workItems);
            model.addAttribute("team", team);
            model.addAttribute("workItemDto", new WorkItemDTO());
            model.addAttribute("statuses", itemStatusService.getAll());
            return "team";
        } catch (EntityNotFoundException | NullPointerException e) {
            return "team";
        }
    }

    @GetMapping
    public String showNewТеамPage(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("teamDto", new TeamDTO());
//        model.addAttribute("members", teamService.getMembers(user.getTeam(), user));
        return "create_team";
    }

    @PostMapping
    public String createTeam(@Valid @ModelAttribute("teamDto") TeamDTO teamDTO,
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
            teamDTO.setOwnerId(user.getId());
            Team team = teamMapper.fromDto(teamDTO);
            teamService.create(team, user);
            user.setTeam(team);
            userService.update(user, user);
            return "redirect:/team/all";
        } catch (DuplicateEntityException e) {
            return "create_team";
        } catch (UnauthorizedOperationException e) {
            return "forbidden_403";
        } catch (EntityNotFoundException e) {
            return "error-404";
        }
    }
}
