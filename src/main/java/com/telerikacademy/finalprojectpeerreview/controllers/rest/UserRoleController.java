package com.telerikacademy.finalprojectpeerreview.controllers.rest;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.ItemStatus;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.UserRole;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserRoleService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user_role")
public class UserRoleController {

    private UserRoleService userRoleService;
    private AuthenticationHelper authenticationHelper;

    @Autowired
    public UserRoleController(UserRoleService userRoleService, AuthenticationHelper authenticationHelper) {
        this.userRoleService = userRoleService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public List<UserRole> getAll() {
        return userRoleService.getAll();
    }

    @GetMapping("/{id}")
    public UserRole getById(@PathVariable int id) {
        try {
            return userRoleService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public UserRole createUserRole(@RequestHeader HttpHeaders headers,
                                   @Valid @RequestBody UserRole userRole) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userRoleService.create(userRole,user);
            return userRole;
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public UserRole update(@RequestHeader HttpHeaders headers,
                             @PathVariable int id,
                             @Valid @RequestBody UserRole userRole) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userRoleService.update(userRole, user);
            return userRole;
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader HttpHeaders headers,
                       @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userRoleService.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}