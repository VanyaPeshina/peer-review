package com.telerikacademy.finalprojectpeerreview.controllers;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.UserDTO;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.models.mappers.UserMapper;
import com.telerikacademy.finalprojectpeerreview.services.UserServiceImpl;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UserController(UserServiceImpl userService, UserMapper userMapper, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping()
    public List<User> getAll(@RequestHeader HttpHeaders headers,
                             @RequestParam(required = false) Optional<String> search) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            return userService.search(search);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public User getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            return userService.getByField("id", id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/requests")
    public List<WorkItem> getAllRequests(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            return userService.getAllRequests(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/user")
    public User createUser(@RequestHeader HttpHeaders headers, @Valid @RequestBody UserDTO userDTO) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            User user = userMapper.fromDto(userDTO);
            userService.create(user, userToAuthenticate);
            return user;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id,
                           @RequestHeader HttpHeaders headers,
                           @Valid @RequestBody UserDTO userDTO) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            userDTO.setId(id);
            User user = userMapper.fromDto(userDTO);
            userService.update(user, userToAuthenticate);
            return user;
        } catch (IOException | DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id,
                           @RequestHeader HttpHeaders headers) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            userService.delete(id, userToAuthenticate);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
