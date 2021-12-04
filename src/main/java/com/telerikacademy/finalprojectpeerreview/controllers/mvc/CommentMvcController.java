/*
package com.telerikacademy.finalprojectpeerreview.controllers.mvc;

import com.telerikacademy.finalprojectpeerreview.exceptions.AuthenticationFailureException;
import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.Comment;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.CommentDTO;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.models.mappers.CommentMapper;
import com.telerikacademy.finalprojectpeerreview.services.CommentService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthenticationHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/comments")
public class CommentMvcController {

    private final CommentService commentService;
    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final WorkItemService workItemService;
    private final CommentMapper commentMapper;

    public CommentMvcController(CommentService commentService, AuthenticationHelper authenticationHelper,
                                UserService userService, WorkItemService workItemService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.workItemService = workItemService;
        this.commentMapper = commentMapper;
    }

    */
/*@GetMapping("/{id}")*//*


    @PostMapping
    public String createComment(@Valid @ModelAttribute("commentDto") CommentDTO commentDTO,
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
            comment.setWorkItem((WorkItem) model.getAttribute("workItem"));
            commentService.create(comment, user);
            return "view_workItem";
        } catch (DuplicateEntityException | IllegalArgumentException e) {
            return "conflict_409";
        } catch (UnauthorizedOperationException e) {
            return "new_workItem";
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "redirect:/login";
        }
    }
}
*/
