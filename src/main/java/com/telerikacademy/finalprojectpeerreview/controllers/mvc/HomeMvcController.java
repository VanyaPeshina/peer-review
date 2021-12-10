package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("SPRING_SECURITY_CONTEXT") != null;
    }

    @GetMapping
    public String showHomePage(Model model) {
        try {
            return "index";
        } catch (HttpClientErrorException e) {
            return "error-404";
        }
    }
}
