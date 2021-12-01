package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.*;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.WorkItemDTO;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.models.mappers.WorkItemMapper;
import com.telerikacademy.finalprojectpeerreview.services.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/work_item")
public class WorkItemMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final WorkItemMapper workItemMapper;
    private final WorkItemService workItemService;
    private final FileStorageService fileStorageService;

    public WorkItemMvcController(AuthenticationHelper authenticationHelper, WorkItemMapper workItemMapper,
                                 WorkItemService workItemService, FileStorageService fileStorageService) {
        this.authenticationHelper = authenticationHelper;
        this.workItemMapper = workItemMapper;
        this.workItemService = workItemService;
        this.fileStorageService = fileStorageService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String showNewWorkItemPage(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }
        model.addAttribute("workItemDto", new WorkItemDTO());
        return "submit_workItem";
    }

    @PostMapping
    public String createWorkItem(@Valid @ModelAttribute("workItemDto") WorkItemDTO dto,
                                 BindingResult result, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
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
            return "redirect:/dashboard";
        } catch (DuplicateEntityException | IllegalArgumentException e) {
            return "conflict_409";
        } catch (UnauthorizedOperationException e) {
            return "new_workItem";
        }
    }

    @GetMapping("/{id}")
    public String getSingleWorkItemPage(@PathVariable int id, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }

        try {
            WorkItem workItem = workItemService.getById(id);
            WorkItemDTO workItemDTO = workItemMapper.toDto(workItem);
            model.addAttribute("workItemId", id);
//            model.addAttribute("parcelDto", parcelDto);
//            model.addAttribute("customer", parcel.getCustomer());
//            model.addAttribute("shipment", shipmentService.getById(parcel.getShipment().getId()));
            return "single_workItem";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error-404";
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/{id}")
    public String updateWorkItem(@PathVariable int id, @Valid @ModelAttribute("workItemDto") WorkItemDTO dto,
                                 BindingResult errors, HttpSession session,
                                 @RequestParam("file")MultipartFile file){
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }
        if (errors.hasErrors()) {
            return "single_workItem";
        }
        try {
            WorkItem workItemToUpdate = workItemMapper.fromDto(dto);
            String fileName = fileStorageService.storeFile(file);
            workItemToUpdate.setFileName(fileName);
            workItemService.update(workItemToUpdate, user);
            return "redirect:/dashboard";
        } catch (EntityNotFoundException e) {
            return "single_workItem";
        } catch (DuplicateEntityException | IllegalArgumentException e) {
            return "conflict_409";
        } catch (UnauthorizedOperationException e) {
            return "unauthorized_error";
        } catch (ChangeNotPossibleException e) {
            return "parcel_forbidden_403";
        }
    }
}