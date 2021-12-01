package com.telerikacademy.finalprojectpeerreview.controllers.mvc;
import com.telerikacademy.finalprojectpeerreview.exceptions.AuthenticationFailureException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.LoginDto;
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

@Controller
@RequestMapping("/login")
public class LoginMvcController {

    private final UserService service;
    private final AuthenticationHelper authenticationHelper;

    public LoginMvcController(UserService service, AuthenticationHelper authenticationHelper) {
        this.service = service;
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String showLoginPage(Model model) {
        model.addAttribute("user", new LoginDto());
        return "login";
    }

    @PostMapping
    public String handleLogin(@Valid @ModelAttribute("user") LoginDto userToLogin,
                              BindingResult bindingResult,
                              HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            authenticationHelper.verifyAuthentication(userToLogin.getUsername(), userToLogin.getPassword());
            session.setAttribute("currentUser", userToLogin.getUsername());
            /*session.setAttribute("currentRole", service.getByUsername(userToLogin.getEmail()).getUserType());*/
            return "redirect:/";
        } catch (AuthenticationFailureException e) {
            bindingResult.rejectValue("username", "auth_error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/";
    }
}
