package com.telerikacademy.finalprojectpeerreview.models.DTOs;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.Serializable;

public class WorkItemDTO implements Serializable {

    private int id;

    private String title;

    private String description;

    private int reviewerId;

    private int statusId;

    private int creatorId;

    private CommonsMultipartFile file;

    public WorkItemDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public CommonsMultipartFile getWork_item() {
        return file;
    }

    public void setWork_item(CommonsMultipartFile file) {
        this.file = file;
    }
}
