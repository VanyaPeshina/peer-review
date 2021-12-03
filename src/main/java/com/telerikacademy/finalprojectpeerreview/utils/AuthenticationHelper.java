package com.telerikacademy.finalprojectpeerreview.utils;

import com.telerikacademy.finalprojectpeerreview.exceptions.AuthenticationFailureException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;

import static com.telerikacademy.finalprojectpeerreview.utils.constants.*;

@Component
public class AuthenticationHelper {

    private final UserService userService;

    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The requested resource requires authentication.");
        }
        try {
            String username = headers.getFirst(AUTHORIZATION_HEADER_NAME);
            return userService.getByField("username", username);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username.");
        }
    }

    public User tryGetUser(HttpSession session) throws EntityNotFoundException {
        String currentUser = (String) session.getAttribute("currentUser");

        if (currentUser == null) {
            throw new AuthenticationFailureException("No user logged in.");
        }

        return userService.getByField("username", currentUser);
    }

    public User verifyAuthentication(String username, String password) {
        try {
            User user = userService.getByField("username", username);
            if (!user.getPassword().equals(password)) {
                throw new AuthenticationFailureException(AUTHENTICATION_FAILURE_MESSAGE);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthenticationFailureException(AUTHENTICATION_FAILURE_MESSAGE);
        }
    }

    public User returnUserFromSession(HttpSession session) throws EntityNotFoundException {
        return userService.getByField("username", session.getAttribute("currentUser").toString());
    }


}
