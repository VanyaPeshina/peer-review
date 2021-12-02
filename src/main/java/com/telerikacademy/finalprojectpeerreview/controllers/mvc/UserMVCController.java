package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.AuthenticationFailureException;
import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.UserDTO;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.mappers.UserMapper;
import com.telerikacademy.finalprojectpeerreview.services.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/users")
public class UserMVCController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;

    public UserMVCController(UserService userService, AuthenticationHelper authenticationHelper, UserMapper userMapper, FileStorageService fileStorageService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping()
    public String showProfile(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }
        try {
            User userToUpdate = userService.getById(user.getId());
            UserDTO userDto = userMapper.toDto(userToUpdate);
            model.addAttribute("userDto", userDto);
            model.addAttribute("user", user);
            return "user";
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }
    }

    @PostMapping()
    public String updateProfile(@Valid @ModelAttribute("userDto") UserDTO dto,
                                BindingResult errors, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }
        if (errors.hasErrors()) {
            return "user";
        }
        try {
           /* model.addAttribute("upload", "/api/users/" + user.getId() + "/file");*/
            dto.setId(user.getId());
            if (dto.getMultipartFile() != null) {
                String fileName = fileStorageService.storeFile(dto.getMultipartFile());
                dto.setPhotoName(fileName);
            }
            User userToUpdate = userMapper.fromDto(dto);
            userService.update(userToUpdate, user);
            return "redirect:/users";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-404";
        } catch (DuplicateEntityException e) {
            return "user_profile_conflict_409";
        } catch (IOException e) {
            e.printStackTrace();
            return "error-404";
        }
    }
}
