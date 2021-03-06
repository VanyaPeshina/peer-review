package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.TeamDTO;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.WorkItemDTO;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.mappers.TeamMapper;
import com.telerikacademy.finalprojectpeerreview.services.contracts.ItemStatusService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
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
@RequestMapping("/team")
public class TeamMvcController {

    private final UserService userService;
    private final TeamService teamService;
    private final TeamMapper teamMapper;
    private final ItemStatusService itemStatusService;
    private final UserHelper userHelper;
    private final WorkItemsHelper workItemsHelper;

    public TeamMvcController(UserService userService,
                             TeamService teamService,
                             TeamMapper teamMapper,
                             ItemStatusService itemStatusService,
                             UserHelper userHelper,
                             WorkItemsHelper workItemsHelper) {
        this.userService = userService;
        this.teamService = teamService;
        this.teamMapper = teamMapper;
        this.itemStatusService = itemStatusService;
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

    @ModelAttribute("user")
    public User populateUser(Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        return user;
    }

    @GetMapping
    public String showTeamPage(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        try {
            model.addAttribute("user", user);
            Team team = teamService.getById(user.getTeam().getId());
            model.addAttribute("teamMembers", teamService.getMembers(team, user));
            model.addAttribute("workItems", teamService.getTeamWorkItems(team));
            model.addAttribute("team", team);
            model.addAttribute("workItemDto", new WorkItemDTO());
            model.addAttribute("statuses", itemStatusService.getAll());
            return "team";
        } catch (EntityNotFoundException | NullPointerException e) {
            return "team";
        }
    }

    @GetMapping("/leave")
    public String leaveTeam(Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        if (user.getTeam() != null) {
            if (workItemsHelper.checkForUnfinishedWorkItems(user)) {
                return "error_leave_team";
            }
        }
        user.setTeam(null);
        try {
            userService.update(user, user);
            return "redirect:/my_dashboard";
        } catch (DuplicateEntityException | UnauthorizedOperationException e) {
            //TODO
            e.printStackTrace();
            return "team";
        }
    }

    @GetMapping("/create")
    public String showNewTeamPage(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("teamDto", new TeamDTO());
//        model.addAttribute("members", teamService.getMembers(user.getTeam(), user));
        return "create_team";
    }

    @PostMapping("/create")
    public String createTeam(@Valid @ModelAttribute("teamDto") TeamDTO teamDTO,
                             BindingResult errors,
                             Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        if (errors.hasErrors()) {
            return "create_team";
        }
        if (user.getTeam() != null) {
            if (workItemsHelper.checkForUnfinishedWorkItems(user)) {
                return "error_leave_team";
            }
        }
        try {
            teamDTO.setOwnerId(user.getId());
            Team team = teamMapper.fromDto(teamDTO);
            teamService.create(team, user);
            user.setTeam(team);
            userService.update(user, user);
            return "redirect:/team";
        } catch (DuplicateEntityException | UnauthorizedOperationException e) {
            errors.rejectValue("name", "name_error", e.getMessage());
            return "create_team";
        } catch (EntityNotFoundException e) {
            return "error-404";
        }
    }
}
