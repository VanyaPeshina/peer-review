package com.telerikacademy.finalprojectpeerreview.controllers.rest;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.TeamDTO;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.mappers.TeamMapper;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final AuthenticationHelper authenticationHelper;
    private final TeamMapper teamMapper;

    public TeamController(TeamService teamService, AuthenticationHelper authenticationHelper, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.authenticationHelper = authenticationHelper;
        this.teamMapper = teamMapper;
    }

    @GetMapping
    public List<Team> getAll() {
        return teamService.getAll();
    }

    @GetMapping("/{id}")
    public Team getById(@PathVariable int id) {
        try {
            return teamService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/members")
    public List<User> getMembers(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Team team = teamService.getById(id);
            return teamService.getMembers(team, user);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public Team createTeam(@RequestHeader HttpHeaders headers,
                           @Valid @RequestBody TeamDTO teamDTO) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            teamDTO.setOwnerId(user.getId());
            Team team = teamMapper.fromDto(teamDTO);
            teamService.create(team, user);
            return team;
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Team update(@RequestHeader HttpHeaders headers,
                       @PathVariable int id,
                       @Valid @RequestBody TeamDTO teamDTO) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            teamDTO.setId(id);
            Team team = teamMapper.fromDto(teamDTO);
            teamService.update(team, user);
            return team;
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/delete/{id}")
    public void delete(@RequestHeader HttpHeaders headers,
                       @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            teamService.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
