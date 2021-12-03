package com.telerikacademy.finalprojectpeerreview.models.mappers;

import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.Comment;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.CommentDTO;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.CommentRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final WorkItemService workItemService;

    public CommentMapper(CommentRepository commentRepository, UserService userService, WorkItemService workItemService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.workItemService = workItemService;
    }

    public Comment fromDto(CommentDTO commentDTO) throws EntityNotFoundException {
        Comment comment;
        if (commentDTO.getId() == 0) {
            comment = new Comment();
        } else {
            comment = commentRepository.getById(commentDTO.getId());
        }
        DTOtoObject(commentDTO, comment);
        return comment;
    }

    private void DTOtoObject(CommentDTO commentDTO, Comment comment) throws EntityNotFoundException {
        if (commentDTO.getComment() != null) {
            comment.setComment(commentDTO.getComment());
        }
        if (comment.getCreator() == null) {
            if (commentDTO.getCreatorId() > 0) {
                User creator = userService.getById(commentDTO.getCreatorId());
                comment.setCreator(creator);
            }
        }
        if (comment.getWorkItem() == null) {
            if (commentDTO.getWorkItemId() > 0) {
                WorkItem workItem = workItemService.getById(commentDTO.getWorkItemId());
                comment.setWorkItem(workItem);
            }
        }
    }
}
