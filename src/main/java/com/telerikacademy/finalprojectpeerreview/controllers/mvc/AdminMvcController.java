package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.FileStorageException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.filestorage.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.models.*;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.UserDTO;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.WorkItemDTO;
import com.telerikacademy.finalprojectpeerreview.models.mappers.UserMapper;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserRoleService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import com.telerikacademy.finalprojectpeerreview.utils.UserHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminMvcController {

    private final UserService userService;
    private final TeamService teamService;
    private final WorkItemService workItemService;
    private final UserHelper userHelper;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;
    private final UserRoleService userRoleService;

    public AdminMvcController(UserService userService,
                              TeamService teamService,
                              WorkItemService workItemService,
                              UserHelper userHelper,
                              UserMapper userMapper,
                              FileStorageService fileStorageService,
                              UserRoleService userRoleService) {
        this.userService = userService;
        this.teamService = teamService;
        this.workItemService = workItemService;
        this.userHelper = userHelper;
        this.userMapper = userMapper;
        this.fileStorageService = fileStorageService;
        this.userRoleService = userRoleService;
    }

    @ModelAttribute("isAdmin")
    public boolean checkForAdmin(Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        return user.getRole().getRole().equals("Admin");
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("SPRING_SECURITY_CONTEXT") != null;
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

    @ModelAttribute("roles")
    public List<UserRole> populateUser() {
        return userRoleService.getAll();
    }

    @GetMapping("/te_am")
    public String showAllTeams(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        try {
            model.addAttribute("user", user);
            List<Team> allTeams = teamService.getAll();
            model.addAttribute("allTeams", allTeams);
            return "all_teams";
        } catch (NullPointerException e) {
            return "team";
        }
    }

    @GetMapping("/workItems")
    public String showAllWorkItems(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        try {
            model.addAttribute("user", user);
            List<WorkItem> allWorkItems = workItemService.getAll();
            model.addAttribute("allWorkItems", allWorkItems);
            model.addAttribute("workItemDto", new WorkItemDTO());
            return "all_workItems";
        } catch (NullPointerException e) {
            return "team";
        }
    }

    @GetMapping("/use_rs")
    public String showAllUsers(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        try {
            model.addAttribute("user", user);
            List<User> allUsers = userService.getAll();
            model.addAttribute("allUsers", allUsers);
            return "all_users";
        } catch (NullPointerException e) {
            return "team";
        }
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable int id, Model model, Principal principal){
        User user = (User) userService.loadUserByUsername(principal.getName());
        try{
            User userToUpdate = userService.getById(id);
            UserDTO userDto = userMapper.toDto(userToUpdate);
            model.addAttribute("userDto", userDto);
            model.addAttribute("user", user);
            return "admin_user";
        }catch (EntityNotFoundException e){
            return "error-404";
        }
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable int id,
                             @Valid @ModelAttribute("userDto") UserDTO dto,
                             BindingResult errors,
                             Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        if (errors.hasErrors()) {
            return "user";
        }
        try {
            dto.setId(id);
            if (dto.getMultipartFile() != null && !dto.getMultipartFile().isEmpty()) {
                String fileName = fileStorageService.storeFile(dto.getMultipartFile());
                dto.setPhotoName(fileName);
            }
            User userToUpdate = userMapper.fromDto(dto);
            userService.update(userToUpdate, user);
            return "redirect:/admin/use_rs";
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "redirect:/login";
        } catch (FileStorageException | DuplicateEntityException | UnauthorizedOperationException e) {
            //TODO
            e.printStackTrace();
            return "user";
        }
    }
}
