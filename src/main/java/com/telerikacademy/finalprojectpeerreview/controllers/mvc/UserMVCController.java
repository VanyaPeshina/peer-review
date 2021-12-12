package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.AuthenticationFailureException;
import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.UserDTO;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.mappers.UserMapper;
import com.telerikacademy.finalprojectpeerreview.services.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.utils.UserHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @ModelAttribute("invitationsForYou")
    public List<Invitation> populateIs(Principal principal) {
        return userHelper.invitationsForYou((User) userService.loadUserByUsername(principal.getName()));
    }

    @GetMapping()
    public String showProfile(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        try {
            User userToUpdate = userService.getById(user.getId());
            UserDTO userDto = userMapper.toDto(userToUpdate);
            model.addAttribute("userDto", userDto);
            model.addAttribute("user", user);
            return "user";
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
    }

    @PostMapping()
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
            return "redirect:/users";
        } catch (DuplicateEntityException e) {
            return "error-404";
        } catch (IOException e) {
            e.printStackTrace();
            return "error-404";
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "redirect:/login";
        }
    }
}
