package com.telerikacademy.finalprojectpeerreview.controllers.rest;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.WorkItemDTO;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.models.mappers.WorkItemMapper;
import com.telerikacademy.finalprojectpeerreview.services.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.services.UserServiceImpl;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/work_items")
public class WorkItemController {

    private static final Logger logger = LoggerFactory.getLogger(WorkItemController.class);

    private final WorkItemService workItemService;
    private final WorkItemMapper workItemMapper;
    private final AuthenticationHelper authenticationHelper;
    private final FileStorageService fileStorageService;

    @Autowired
    public WorkItemController(WorkItemService workItemService, WorkItemMapper workItemMapper,
                              AuthenticationHelper authenticationHelper, FileStorageService fileStorageService) {
        this.workItemService = workItemService;
        this.workItemMapper = workItemMapper;
        this.authenticationHelper = authenticationHelper;
        this.fileStorageService = fileStorageService;
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

    @PostMapping()
    public WorkItem createWorkItem(@RequestHeader HttpHeaders headers,
                                   @Valid @RequestBody WorkItemDTO workItemDTO) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            WorkItem workItem = workItemMapper.fromDto(workItemDTO);
            workItemService.create(workItem, userToAuthenticate);
            return workItem;
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public WorkItem updateWorkItem(@PathVariable int id,
                                   @RequestHeader HttpHeaders headers,
                                   @Valid @RequestBody WorkItemDTO workItemDTO) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            workItemDTO.setId(id);
            WorkItem workItem = workItemMapper.fromDto(workItemDTO);
            workItemService.update(workItem, userToAuthenticate);
            return workItemService.getById(id);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{id}/uploadFile")
    public void uploadFile(@PathVariable int id,
                           @RequestHeader HttpHeaders headers,
                           @RequestParam("file") MultipartFile file) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            WorkItem workItem = workItemService.getById(id);
            String fileName = fileStorageService.storeFile(file);
            workItem.setFileName(fileName);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/downloadFile/")
                    .path(fileName)
                    .toUriString();
            workItem.setFileDownloadUri(fileDownloadUri);
            workItemService.update(workItem, userToAuthenticate);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable int id,
                                                 @PathVariable String fileName,
                                                 HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
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
                                 @RequestParam(required = false) Optional<String> reviewer,
                                 @RequestParam(required = false) Optional<String> sort) {
        return workItemService.filter(name, status, reviewer, sort);
    }
}
