package com.telerikacademy.finalprojectpeerreview.models.DTOs;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.io.Serializable;

import static com.telerikacademy.finalprojectpeerreview.utils.constants.*;

public class WorkItemDTO implements Serializable {

    private int id;

    @Size(min = 10, max = 80, message = ITEM_TITLE_SHOULD_BE_BETWEEN)
    private String title;

    @Size (min = 20, max = 65535, message = DESCRIPTION_SHOULD_BE_BETWEEN)
    private String description;

    private int reviewerId;

    private int statusId;

    private int creatorId;

    private MultipartFile multipartFile;

    private String fileName;

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

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
