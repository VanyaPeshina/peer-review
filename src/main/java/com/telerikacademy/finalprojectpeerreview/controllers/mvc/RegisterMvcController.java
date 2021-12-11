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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/register")
public class RegisterMvcController {

    private final UserMapper userMapper;
    private final UserService userService;

    public RegisterMvcController(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
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
            //TODO: Why we harcoded this???
            User creator = userService.getById(1);
            User newUser = userMapper.fromDto(userDTO);
            userService.create(newUser, creator);

            //sign up
            userService.signUpUser(newUser);
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

    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token) throws EntityNotFoundException {
        return userService.confirmToken(token);
    }
}

