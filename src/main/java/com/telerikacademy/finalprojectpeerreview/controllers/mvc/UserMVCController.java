package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.*;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.UserDTO;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.mappers.UserMapper;
import com.telerikacademy.finalprojectpeerreview.filestorage.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
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
@RequestMapping("/users")
public class UserMVCController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;
    private final UserHelper userHelper;

    public UserMVCController(UserService userService,
                             UserMapper userMapper,
                             FileStorageService fileStorageService, UserHelper userHelper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.fileStorageService = fileStorageService;
        this.userHelper = userHelper;
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

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        UserDTO userDto = userMapper.toDto(user);
        model.addAttribute("userDto", userDto);
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("userDto") UserDTO dto,
                                BindingResult errors,
                                Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if (errors.hasErrors()) {
            return "user";
        }
        try {
            dto.setId(user.getId());
            if (dto.getMultipartFile() != null && !dto.getMultipartFile().isEmpty()) {
                String fileName = fileStorageService.storeFile(dto.getMultipartFile());
                dto.setPhotoName(fileName);
            }
            User userToUpdate = userMapper.fromDto(dto);
            userService.update(userToUpdate, user);
            return "redirect:/users/updated";
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "redirect:/login";
        } catch (FileStorageException | DuplicateEntityException | UnauthorizedOperationException e) {
            //TODO
            e.printStackTrace();
            return "user";
        }
    }

    @GetMapping("/updated")
    public String showUpdatedProfile(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        UserDTO userDto = userMapper.toDto(user);
        model.addAttribute("userDto", userDto);
        model.addAttribute("user", user);
        return "updated_user";
    }

    @GetMapping("/change_password")
    public String showPage(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        UserDTO userDto = userMapper.toDto(user);
        model.addAttribute("userDto", userDto);
        model.addAttribute("user", user);
        return "change_password";
    }

    @PostMapping("/change_password")
    public String updatePassword(@Valid @ModelAttribute("userDto") UserDTO dto,
                                 BindingResult errors,
                                 Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        if (dto.getPassword().equals(dto.getSecondPassword())) {
            errors.rejectValue("secondPassword", "password_error",
                    "New password can't be same as old password");
            /*return "change_password";*/
        }
        if (errors.hasErrors()) {
            return "change_password";
        }
        try {
            dto.setId(user.getId());
            User userToUpdate = userMapper.fromDto(dto);
            userService.update(userToUpdate, user);
            return "redirect:/users/updated";
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "redirect:/login";
        } catch (DuplicateEntityException | UnauthorizedOperationException e) {
            //TODO
            e.printStackTrace();
            return "user";
        } catch (WrongPasswordException e) {
            return "wrong_password";
        }
    }
}
