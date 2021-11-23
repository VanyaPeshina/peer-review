package com.telerikacademy.finalprojectpeerreview.controllers;

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

    @PostMapping("/work_item")
    public WorkItem createWorkItem(@RequestHeader HttpHeaders headers, @Valid @RequestBody WorkItemDTO workItemDTO) {
        try {
            /*User userToAuthenticate = authenticationHelper.tryGetUser(headers);*/
            User userToAuthenticate = userService.getById(1);
            WorkItem workItem = workItemMapper.fromDto(workItemDTO);
            workItemService.create(workItem, userToAuthenticate);
            return workItem;
        } catch (FileNotFoundException | SQLException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/work_item/{id}")
    public WorkItem updateWorkItem(@PathVariable int id,
                                   @RequestHeader HttpHeaders headers,
                                   @RequestParam CommonsMultipartFile[] files) {
        try {
         /*   User userToAuthenticate = authenticationHelper.tryGetUser(headers);*/
            User userToAuthenticate = userService.getById(1);
            if (files != null && files.length > 0) {
                for (CommonsMultipartFile file : files) {
                    WorkItemDTO workItemDTO = new WorkItemDTO();
                    workItemDTO.setId(id);
                    workItemDTO.setWork_item(file);
                    WorkItem workItem = workItemMapper.fromDto(workItemDTO);
                    workItemService.update(workItem, userToAuthenticate);
                }
            }
            return workItemService.getById(id);
            //TODO remove the SQL exception from here
        } catch (FileNotFoundException | SQLException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
