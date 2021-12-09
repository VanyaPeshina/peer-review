package com.telerikacademy.finalprojectpeerreview.controllers.rest;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.UserDTO;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.models.mappers.UserMapper;
import com.telerikacademy.finalprojectpeerreview.services.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, AuthenticationHelper authenticationHelper,
                          FileStorageService fileStorageService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping()
    public List<User> getAll(@RequestHeader HttpHeaders headers,
                             @RequestParam(required = false) Optional<String> search) {
        try {
            authenticationHelper.tryGetUser(headers);
            return userService.search(search);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public User getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            return userService.getByField("id", id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> downloadFile(@PathVariable int id, HttpServletRequest request) {
        Resource resource = null;
        try {
            // Load file as Resource
            User user = userService.getById(id);
            resource = fileStorageService.loadFileAsResource(user.getPhotoName());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
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

    @GetMapping("/{id}/requests")
    public List<WorkItem> getAllRequests(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        authenticationHelper.tryGetUser(headers);
        return userService.getAllRequests(id);
    }

    @PostMapping()
    public User createUser(@RequestHeader HttpHeaders headers, @Valid @RequestBody UserDTO userDTO) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            User user = userMapper.fromDto(userDTO);
            userService.create(user, userToAuthenticate);
            return user;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
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
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{id}/file")
    public void uploadPhoto(@PathVariable int id, @RequestHeader HttpHeaders headers,
                            @RequestParam("file") MultipartFile file) {
        try {
            User userToAuthenticate = authenticationHelper.tryGetUser(headers);
            User user = userService.getById(id);
            String fileName = fileStorageService.storeFile(file);
            user.setPhotoName(fileName);
            userService.update(user, userToAuthenticate);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/delete/{id}")
    public void deleteUser(@PathVariable int id, @RequestHeader HttpHeaders headers) {
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
