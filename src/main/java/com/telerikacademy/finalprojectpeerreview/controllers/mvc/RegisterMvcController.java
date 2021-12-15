package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.ConfirmationToken;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.UserDTO;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.mappers.UserMapper;
import com.telerikacademy.finalprojectpeerreview.services.contracts.ConfirmationTokenService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/register")
public class RegisterMvcController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;

    public RegisterMvcController(UserMapper userMapper,
                                 UserService userService,
                                 ConfirmationTokenService confirmationTokenService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
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

            //pass token
            userService.create(newUser, creator);
            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    newUser);
            confirmationTokenService.create(confirmationToken, newUser);

            //sign up
            userService.signUpUser(newUser, confirmationToken);
            return "verify_email";
        } catch (DuplicateEntityException | IllegalArgumentException e) {
            errors.rejectValue("password", "email_error", e.getMessage());
            return "register";
        } catch (IOException | EntityNotFoundException e) {
            return "error-404";
        }
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token) throws EntityNotFoundException {
        try {
            userService.confirmToken(token);
            return "redirect:/login";
        } catch (DuplicateEntityException | UnauthorizedOperationException e) {
            //TODO
            e.printStackTrace();
            return "redirect:/register/confirm";
        }
    }
}

