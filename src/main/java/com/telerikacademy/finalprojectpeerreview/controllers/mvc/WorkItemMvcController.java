package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.*;
import com.telerikacademy.finalprojectpeerreview.models.Comment;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.CommentDTO;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.WorkItemDTO;
import com.telerikacademy.finalprojectpeerreview.models.ItemStatus;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.models.mappers.CommentMapper;
import com.telerikacademy.finalprojectpeerreview.models.mappers.WorkItemMapper;
import com.telerikacademy.finalprojectpeerreview.services.CommentService;
import com.telerikacademy.finalprojectpeerreview.services.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.ItemStatusService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/work_item")
public class WorkItemMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final WorkItemMapper workItemMapper;
    private final WorkItemService workItemService;
    private final FileStorageService fileStorageService;
    private final TeamService teamService;
    private final ItemStatusService itemStatusService;
    private final UserService userService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public WorkItemMvcController(AuthenticationHelper authenticationHelper, WorkItemMapper workItemMapper,
                                 WorkItemService workItemService, FileStorageService fileStorageService,
                                 TeamService teamService, ItemStatusService itemStatusService, UserService userService,
                                 CommentService commentService, CommentMapper commentMapper) {
        this.authenticationHelper = authenticationHelper;
        this.workItemMapper = workItemMapper;
        this.workItemService = workItemService;
        this.fileStorageService = fileStorageService;
        this.teamService = teamService;
        this.itemStatusService = itemStatusService;
        this.userService = userService;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("status")
    public List<ItemStatus> populateWarehouses() {
        return itemStatusService.getAll();
    }

    @GetMapping("/all")
    public String showWorkItemsPage(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("workItems", workItemService.getAll()
                .stream().filter(workItem -> workItem.getCreator().getId() == user.getId())
                .collect(Collectors.toList()));
        model.addAttribute("workItemDto", new WorkItemDTO());
        model.addAttribute("users", userService.getAll()
                .stream()
                .filter(user1 -> user1.getTeam().getId() == user.getTeam().getId())
                .collect(Collectors.toList()));
        model.addAttribute("statuses", itemStatusService.getAll());
        return "workItems";
    }

    @GetMapping("/for_review")
    public String workItemForReview(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("workItemsForReview", workItemService.getAll().stream()
                .filter(workItem -> workItem.getTeam().getId() == user.getTeam().getId())
                .filter(workItem -> workItem.getReviewer().getId() == user.getId())
                .collect(Collectors.toList()));
        return "for_review";
    }

    @GetMapping("/change_request")
    public String workItemForChange(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("workItemsForChange", workItemService.getAll().stream()
                .filter(workItem -> workItem.getTeam().getId() == user.getTeam().getId())
                .filter(workItem -> workItem.getStatus().getStatus().equals("Change Requested"))
                .collect(Collectors.toList()));
        return "change_request";
    }

    @GetMapping("/view/{id}")
    public String viewWorkItemPage(@PathVariable int id, Model model,
                                   HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        try {
            model.addAttribute("user", user);
            WorkItem workItem = workItemService.getById(id);
            model.addAttribute("workItem", workItem);
            model.addAttribute("comments", commentService.getAll()
                    .stream()
                    .filter(comment -> comment.getWorkItem().getId() == workItem.getId())
                    .collect(Collectors.toList()));
            model.addAttribute("commentDto", new CommentDTO());
            return "view_workItem";
        } catch (EntityNotFoundException e) {
            return "error-404";
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/view/{id}")
    public String createComment(@PathVariable int id,
                                @Valid @ModelAttribute("commentDto") CommentDTO commentDTO,
                                BindingResult result, HttpSession session, Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            return "view_workItem";
        }
        try {
            Comment comment = commentMapper.fromDto(commentDTO);
            comment.setCreator(user);
            comment.setWorkItem(workItemService.getById(id));
            commentService.create(comment, user);
            String redirect = "redirect:/work_item/view/" + id;
            return redirect;
        } catch (DuplicateEntityException | IllegalArgumentException e) {
            return "conflict_409";
        } catch (UnauthorizedOperationException e) {
            return "new_workItem";
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "redirect:/login";
        }
    }

    @GetMapping
    public String showNewWorkItemPage(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("workItemDto", new WorkItemDTO());
        model.addAttribute("members", teamService.getMembers(user.getTeam(), user));
        return "submit_workItem";
    }

    @PostMapping
    public String createWorkItem(@Valid @ModelAttribute("workItemDto") WorkItemDTO dto,
                                 BindingResult result, HttpSession session, Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            return "submit_workItem";
        }
        try {
            WorkItem workItem = workItemMapper.fromDto(dto);
            workItem.setCreator(user);
            workItem.setTeam(user.getTeam());
            workItemService.create(workItem, user);
            return "redirect:/work_item/all";
        } catch (DuplicateEntityException | IllegalArgumentException e) {
            return "conflict_409";
        } catch (UnauthorizedOperationException e) {
            return "new_workItem";
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "redirect:/login";
        }
    }

    @GetMapping("/{id}")
    public String getSingleWorkItemPage(@PathVariable int id, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        try {
            WorkItem workItem = workItemService.getById(id);
            WorkItemDTO workItemDTO = workItemMapper.toDto(workItem);
            model.addAttribute("user", user);
            model.addAttribute("workItemDTO", workItemDTO);
            model.addAttribute("members", teamService.getMembers(user.getTeam(), user));
            return "single_workItem";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-404";
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/{id}")
    public String updateWorkItem(@PathVariable int id, @Valid @ModelAttribute("workItemDTO") WorkItemDTO dto,
                                 BindingResult errors, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        if (dto.getStatusId() == 3 || dto.getStatusId() == 5) {
            if (dto.getComment().isEmpty()) {
                errors.rejectValue("comment", "error.workItemDTO.comment", "can't be empty");
            }
        }
        if (errors.hasErrors()) {
            return "single_workItem";
        }
        try {
            WorkItem workItemToUpdate = workItemMapper.fromDto(dto);
            if (!dto.getComment().isEmpty()) {
                CommentDTO commentDTO = commentMapper.toDTO(dto.getComment(), user, workItemToUpdate);
                commentService.create(commentMapper.fromDto(commentDTO), user);
            }
            workItemService.update(workItemToUpdate, user);
            return "redirect:/work_item/all";
        } catch (DuplicateEntityException | IllegalArgumentException e) {
            return "conflict_409";
        } catch (UnauthorizedOperationException e) {
            return "unauthorized_error";
        } catch (ChangeNotPossibleException e) {
            return "parcel_forbidden_403";
        } catch (EntityNotFoundException e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteWorkItem(@PathVariable int id, Model model, HttpSession session) {
        User userToAuthenticate;
        try {
            userToAuthenticate = authenticationHelper.tryGetUser(session);
        } catch (UnauthorizedOperationException | EntityNotFoundException e) {
            return "redirect:/login";
        }
        try {
            workItemService.delete(id, userToAuthenticate);
            return "redirect:/work_item/all";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-404";
        }
    }

    @PostMapping("/filter")
    public String filter(@ModelAttribute WorkItemDTO workItemDTO, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "redirect:/login";
        }
        List<WorkItem> filteredWorkItems = workItemService.filterMVC(
                Optional.of(workItemDTO.getCreatorId()),
                Optional.of(workItemDTO.getReviewerId()),
                Optional.of(workItemDTO.getStatusId()));

        model.addAttribute("filteredWorkItems", filteredWorkItems);
        model.addAttribute("user", user);
        return "filtered_work_items";
    }
}