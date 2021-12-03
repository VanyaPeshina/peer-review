package com.telerikacademy.finalprojectpeerreview.controllers.mvc;


import com.telerikacademy.finalprojectpeerreview.exceptions.AuthenticationFailureException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.services.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardMvcController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardMvcController.class);

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final FileStorageService fileStorageService;
    private final WorkItemService workItemService;
    private final TeamService teamService;

    public DashboardMvcController(UserService userService, AuthenticationHelper authenticationHelper,
                                  FileStorageService fileStorageService, WorkItemService workItemService, TeamService teamService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.fileStorageService = fileStorageService;
        this.workItemService = workItemService;
        this.teamService = teamService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String showDashboardPage(Model model, HttpSession session, HttpServletRequest request) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);

        List<WorkItem> workItemsCreatedByUser = workItemService.getAll()
                .stream()
                .filter(workItem -> workItem.getCreator().getId() == user.getId())
                .collect(Collectors.toList());

        List<WorkItem> teamItems = workItemService.getAll()
                .stream()
                .filter(workItem -> workItem.getTeam().getId() == user.getTeam().getId())
                .collect(Collectors.toList());

        List<WorkItem> workItemsForReview = userService.getAllRequests(user.getId())
                .stream()
                .filter(workItem -> workItem.getReviewer() != null)
                .filter(workItem -> workItem.getReviewer().getId() == user.getId())
                .collect(Collectors.toList());

        List<WorkItem> itemsNeedingChange = userService.getAllRequests(user.getId())
                .stream()
                .filter(workItem -> workItem.getStatus().getStatus().equals("Change Requested"))
                .collect(Collectors.toList());

        model.addAttribute("workItems", workItemsCreatedByUser);
        model.addAttribute("teamItems", teamItems);
        model.addAttribute("waitingForReview", workItemsForReview);
        model.addAttribute("needingChange", itemsNeedingChange);
        model.addAttribute("members", teamService.getMembers(user.getTeam(), user));

        model.addAttribute("photo", "/api/users/" + user.getId() + "/photo");
        return "dashboard";
    }

   /* @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
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
    }*/
}
