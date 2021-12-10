package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.UserDTO;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.mappers.UserMapper;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/register")
public class RegisterMvcController {

    private final UserMapper mapper;
    private final UserService service;

    public RegisterMvcController(UserMapper mapper, UserService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("SPRING_SECURITY_CONTEXT") != null;
    }

    @GetMapping
    public String newCustomer(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping
    public String createCustomer(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult errors) {
        if (errors.hasErrors()) {
            return "register";
        }
//        if (!userDTO.getPassword().equals(userDTO.getSecondPassword())) {
//            errors.rejectValue("secondPassword", "password_error",
//                    "Password confirmation should be same as password");
//            return "register";
//        }
        try {
            User creator = service.getById(1);
            User newUser = mapper.fromDto(userDTO);
            service.create(newUser, creator);
            return "redirect:/";
        } catch (DuplicateEntityException e) {
            errors.rejectValue("email", "username_error", e.getMessage());
            return "register";
        } catch (UnauthorizedOperationException e) {
            return "forbidden_403";
        } catch (IOException | EntityNotFoundException e) {
            return "error-404";
        }
    }
}

