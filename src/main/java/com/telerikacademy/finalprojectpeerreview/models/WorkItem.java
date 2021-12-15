package com.telerikacademy.finalprojectpeerreview.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "work_items")
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private ItemStatus status;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_download_uri")
    private String fileDownloadUri;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Transient
    private String downloadForMvc;

    public WorkItem() {
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

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getDownloadForMvc() {
        return "/api/work_items/"+ getId() + "/downloadFile/" + getFileName();
    }

    public void setDownloadForMvc(String downloadForMvc) {
        this.downloadForMvc = downloadForMvc;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkItem workItem = (WorkItem) o;
        return Objects.equals(title, workItem.title)
                && Objects.equals(description, workItem.description)
                && Objects.equals(reviewer, workItem.reviewer)
                && Objects.equals(status.getStatus(), workItem.getStatus().getStatus())
                && Objects.equals(creator, workItem.getCreator())
                && Objects.equals(fileName, workItem.getFileName())
                && Objects.equals(team, workItem.team);
    }
}
