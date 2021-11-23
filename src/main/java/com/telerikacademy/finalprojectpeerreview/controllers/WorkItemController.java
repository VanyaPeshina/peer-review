package com.telerikacademy.finalprojectpeerreview.controllers;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.WorkItemDTO;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.models.mappers.WorkItemMapper;
import com.telerikacademy.finalprojectpeerreview.services.UserServiceImpl;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/work_items")
public class WorkItemController {

    private final UserServiceImpl userService;
    private final WorkItemService workItemService;
    private final WorkItemMapper workItemMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public WorkItemController(UserServiceImpl userService, WorkItemService workItemService, WorkItemMapper workItemMapper,
                              AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.workItemService = workItemService;
        this.workItemMapper = workItemMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping()
    public List<WorkItem> getAll(@RequestHeader HttpHeaders headers,
                                 @RequestParam(required = false) Optional<String> search) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            return workItemService.search(search);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public WorkItem getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            return workItemService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/work_item")
    public WorkItem createWorkItem(@RequestHeader HttpHeaders headers, @Valid @RequestBody WorkItemDTO workItemDTO) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            WorkItem workItem = workItemMapper.fromDto(workItemDTO);
            workItemService.create(workItem, userToAuthenticate);
            return workItem;
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        /* catch (FileNotFoundException | SQLException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }*/
    }

    @PutMapping("/work_item/{id}")
    public WorkItem updateWorkItem(@PathVariable int id,
                                   @RequestHeader HttpHeaders headers,
                                   @Valid @RequestBody WorkItemDTO workItemDTO
                                   /*@RequestParam CommonsMultipartFile[] files*/) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
           /* if (files != null && files.length > 0) {
                for (CommonsMultipartFile file : files) {
                    WorkItemDTO workItemDTO = new WorkItemDTO();
                    workItemDTO.setId(id);
                    workItemDTO.setWork_item(file);
                    WorkItem workItem = workItemMapper.fromDto(workItemDTO);
                    workItemService.update(workItem, userToAuthenticate);
                }
            }*/
            workItemDTO.setId(id);
            WorkItem workItem = workItemMapper.fromDto(workItemDTO);
            return workItemService.getById(id);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        /*catch (FileNotFoundException | SQLException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }*/
    }

    @DeleteMapping("/{id}")
    public void deleteWorkItem(@PathVariable int id,
                               @RequestHeader HttpHeaders headers) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            workItemService.delete(id, userToAuthenticate);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/filter")
    public List<WorkItem> filter(@RequestParam(required = false) Optional<String> name,
                                 @RequestParam(required = false) Optional<String> status,
                                 @RequestParam(required = false) Optional<String> sort) {
        return workItemService.filter(name, status, sort);
    }
}
