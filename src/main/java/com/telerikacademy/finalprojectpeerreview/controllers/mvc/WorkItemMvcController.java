package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.*;
import com.telerikacademy.finalprojectpeerreview.models.*;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.CommentDTO;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.WorkItemDTO;
import com.telerikacademy.finalprojectpeerreview.models.mappers.CommentMapper;
import com.telerikacademy.finalprojectpeerreview.models.mappers.WorkItemMapper;
import com.telerikacademy.finalprojectpeerreview.services.CommentService;
import com.telerikacademy.finalprojectpeerreview.services.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.ItemStatusService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import com.telerikacademy.finalprojectpeerreview.utils.UserHelper;
import com.telerikacademy.finalprojectpeerreview.utils.WorkItemsHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/work_item")
public class WorkItemMvcController {

    private final WorkItemMapper workItemMapper;
    private final WorkItemService workItemService;
    private final FileStorageService fileStorageService;
    private final TeamService teamService;
    private final ItemStatusService itemStatusService;
    private final UserService userService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserHelper userHelper;
    private final WorkItemsHelper workItemsHelper;

    public WorkItemMvcController(WorkItemMapper workItemMapper,
                                 WorkItemService workItemService,
                                 FileStorageService fileStorageService,
                                 TeamService teamService,
                                 ItemStatusService itemStatusService,
                                 UserService userService,
                                 CommentService commentService,
                                 CommentMapper commentMapper,
                                 UserHelper userHelper, WorkItemsHelper workItemsHelper) {
        this.workItemMapper = workItemMapper;
        this.workItemService = workItemService;
        this.fileStorageService = fileStorageService;
        this.teamService = teamService;
        this.itemStatusService = itemStatusService;
        this.userService = userService;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.userHelper = userHelper;
        this.workItemsHelper = workItemsHelper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("SPRING_SECURITY_CONTEXT") != null;
    }

    @ModelAttribute("invitationsForYou")
    public List<Invitation> populateIs(Principal principal) {
        return userHelper.invitationsForYou((User) userService.loadUserByUsername(principal.getName()));
    }

//    @ModelAttribute("user")
//    public User userPhoto(Principal principal) {
//        return (User) userService.loadUserByUsername(principal.getName());
//    }

    @ModelAttribute("statuses")
    public List<ItemStatus> populateWarehouses() {
        return itemStatusService.getAll();
    }

    @GetMapping
    public String showWorkItemsPage(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("workItemDto", new WorkItemDTO());
        model.addAttribute("users", userHelper.getTeamMembersWhenTeamIsUnknown(user));
        model.addAttribute("statuses", itemStatusService.getAll());

        List<WorkItem> workItems = workItemsHelper.getWorkItemsCreatedByUser(user);
        model.addAttribute("workItems", workItems);
        model.addAttribute("downloadUris", workItemsHelper.getDownloadUris(workItems));

        return "workItems";
    }

    @GetMapping("/for_review")
    public String workItemForReview(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("workItemsForReview", workItemsHelper.getWorkItemsForReview(user));

        return "for_review";
    }

    @GetMapping("/change_request")
    public String workItemForChange(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("workItemsForChange", workItemsHelper.getItemsNeedingChange(user));

        return "change_request";
    }

    @GetMapping("/view/{id}")
    public String viewWorkItemPage(@PathVariable int id,
                                   Model model,
                                   Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        try {
            model.addAttribute("user", user);
            WorkItem workItem = workItemService.getById(id);
            model.addAttribute("workItem", workItem);
            model.addAttribute("comments", workItemsHelper.getComments(workItem));
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
                                BindingResult result,
                                Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

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

    @GetMapping("/submit")
    public String showNewWorkItemPage(Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("workItemDto", new WorkItemDTO());
        model.addAttribute("members", userHelper.getTeamMembersWithoutTheUser(user));

        return "submit_workItem";
    }

    @PostMapping("/submit")
    public String createWorkItem(@Valid @ModelAttribute("workItemDto") WorkItemDTO dto,
                                 BindingResult result,
                                 Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

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
    public String getSingleWorkItemPage(@PathVariable int id, Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        try {
            WorkItem workItem = workItemService.getById(id);
            WorkItemDTO workItemDTO = workItemMapper.toDto(workItem);
            model.addAttribute("user", user);
            model.addAttribute("workItemDTO", workItemDTO);
            model.addAttribute("members", userHelper.getTeamMembersWithoutTheUser(user));
            model.addAttribute("comments", workItemsHelper.getComments(workItem));

            return "single_workItem";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-404";
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/{id}")
    public String updateWorkItem(@PathVariable int id,
                                 @Valid @ModelAttribute("workItemDTO") WorkItemDTO dto,
                                 BindingResult errors,
                                 Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());

        if (dto.getStatusId() == 3 || dto.getStatusId() == 5) {
            if (dto.getComment().isEmpty()) {
                errors.rejectValue("comment", "error.workItemDTO.comment", "can't be empty");
            }
        }
        if (errors.hasErrors()) {
            //TODO не показва грешката след redirect-ване
//            String redirect = "redirect:/work_item/" + id;
            return "single_workItem";
        }
        if (dto.getMultipartFile() != null && !dto.getMultipartFile().isEmpty()) {
            String fileName = fileStorageService.storeFile(dto.getMultipartFile());
            dto.setFileName(fileName);
        }
        try {
            WorkItem workItemToUpdate = workItemMapper.fromDto(dto);
            if (!dto.getComment().isEmpty()) {
                CommentDTO commentDTO = commentMapper.toDTO(dto.getComment(), user, workItemToUpdate);
                commentService.create(commentMapper.fromDto(commentDTO), user);
            }
            workItemService.update(workItemToUpdate, user);
            return "redirect:/work_item";
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
    public String deleteWorkItem(@PathVariable int id, Model model, Principal principal) {
        User userToAuthenticate = (User) userService.loadUserByUsername(principal.getName());
        try {
            workItemService.delete(id, userToAuthenticate);
            return "redirect:/work_item/all";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-404";
        }
    }

    @PostMapping("/filter")
    public String filter(@ModelAttribute WorkItemDTO workItemDTO, Model model, Principal principal) {
        User user = (User) userService.loadUserByUsername(principal.getName());
        List<WorkItem> filteredWorkItems = workItemService.filterMVC(
                Optional.of(workItemDTO.getCreatorId()),
                Optional.of(workItemDTO.getReviewerId()),
                Optional.of(workItemDTO.getStatusId()));

        if (!filteredWorkItems.isEmpty()) {
            filteredWorkItems = filteredWorkItems.stream()
                    .filter(workItem -> workItem.getCreator().getId() == user.getId())
                    .collect(Collectors.toList());
        }

        model.addAttribute("filteredWorkItems", filteredWorkItems);
        model.addAttribute("user", user);

        return "filtered_work_items";
    }
}