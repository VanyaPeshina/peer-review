package com.telerikacademy.finalprojectpeerreview.utils;

import com.telerikacademy.finalprojectpeerreview.models.Comment;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.services.CommentService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkItemsHelper {

    private final WorkItemService workItemService;
    private final CommentService commentService;

    @Autowired
    public WorkItemsHelper(WorkItemService workItemService, CommentService commentService) {
        this.workItemService = workItemService;
        this.commentService = commentService;
    }

    public List<WorkItem> getItemsNeedingChange(User user) {
        if (user.getTeam() != null) {
            if (workItemService.getAll().size() == 0) {
                return workItemService.getAll();
            }
            return workItemService.getAll().stream()
                    .filter(workItem -> workItem.getTeam().getId() == user.getTeam().getId())
                    .filter(workItem -> workItem.getStatus().getStatus().equals("Change Requested"))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<WorkItem> getWorkItemsForReview(User user) {
        if (user.getTeam() != null) {
            if (workItemService.getAll().size() == 0) {
                return workItemService.getAll();
            }
            return workItemService.getAll().stream()
                    .filter(workItem -> workItem.getTeam().getId() == user.getTeam().getId())
                    .filter(workItem -> workItem.getReviewer().getId() == user.getId())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<WorkItem> getTeamsItems(User user) {
        if (user.getTeam() != null) {
            if (workItemService.getAll().size() == 0) {
                return workItemService.getAll();
            }
            return workItemService.getAll()
                    .stream()
                    .filter(workItem -> workItem.getTeam().getId() == user.getTeam().getId())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<WorkItem> getWorkItemsCreatedByUser(User user) {
        if (workItemService.getAll().size() == 0) {
            return workItemService.getAll();
        }
        return workItemService.getAll()
                .stream()
                .filter(workItem -> workItem.getCreator().getId() == user.getId())
                .collect(Collectors.toList());
    }

    public List<String> getDownloadUris(List<WorkItem> workItems) {
        List<String> workItemsDownloadUris = new ArrayList<>();
        for (WorkItem workItem : workItems) {
            workItemsDownloadUris
                    .add("/api/work_item/" + workItem.getId() + "/downloadFile/{" + workItem.getFileName() + ":.+}");
        }
        return workItemsDownloadUris;
    }

    public List<Comment> getComments(WorkItem workItem) {
        return commentService.getAll()
                .stream()
                .filter(comment -> comment.getWorkItem().getId() == workItem.getId())
                .collect(Collectors.toList());
    }

    public boolean checkForUnfinishedWorkItems(User user) {
        List<WorkItem> itemsToCheck = workItemService.getAll()
                .stream()
                .filter(workItem -> workItem.getStatus().getStatus().equals("Change Requested"))
                .collect(Collectors.toList());

        itemsToCheck.addAll(workItemService.getAll().stream().
                filter(workItem -> workItem.getStatus().getStatus().equals("Pending"))
                .collect(Collectors.toList()));

        itemsToCheck.addAll(workItemService.getAll().stream().
                filter(workItem -> workItem.getStatus().getStatus().equals("Under Review"))
                .collect(Collectors.toList()));

        return itemsToCheck.stream()
                .anyMatch(workItem -> workItem.getCreator().getUsername().equals(user.getUsername()) ||
                        workItem.getReviewer().getUsername().equals(user.getUsername()));
    }
}
