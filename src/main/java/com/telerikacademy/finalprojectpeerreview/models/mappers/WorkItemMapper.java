package com.telerikacademy.finalprojectpeerreview.models.mappers;

import com.telerikacademy.finalprojectpeerreview.models.DTOs.WorkItemDTO;
import com.telerikacademy.finalprojectpeerreview.models.ItemStatus;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.ItemStatusRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.WorkItemRepository;
import com.telerikacademy.finalprojectpeerreview.services.FileStorageService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.ItemStatusService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkItemMapper {

    private final WorkItemRepository workItemRepository;
    private final UserRepository userRepository;
    private final ItemStatusService statusService;
    private final TeamService teamService;
    private final ItemStatusRepository itemStatusRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public WorkItemMapper(WorkItemRepository workItemRepository, UserRepository userRepository,
                          ItemStatusService statusService, TeamService teamService,
                          ItemStatusRepository itemStatusRepository, FileStorageService fileStorageService) {
        this.workItemRepository = workItemRepository;
        this.userRepository = userRepository;
        this.statusService = statusService;
        this.teamService = teamService;
        this.itemStatusRepository = itemStatusRepository;
        this.fileStorageService = fileStorageService;
    }

    public WorkItem fromDto(WorkItemDTO workItemDTO){
        WorkItem workItem;
        if (workItemDTO.getId() == 0) {
            workItem = new WorkItem();
        } else {
            workItem = workItemRepository.getById(workItemDTO.getId());
        }
        DTOtoObject(workItemDTO, workItem);
        return workItem;
    }

    public WorkItemDTO toDto(WorkItem workItem) {
        WorkItemDTO workItemDTO = new WorkItemDTO();
        workItemDTO.setCreatorId(workItem.getId());
        workItemDTO.setDescription(workItem.getDescription());
        workItemDTO.setTitle(workItem.getTitle());
        workItemDTO.setReviewerId(workItem.getReviewer().getId());
        workItemDTO.setStatusId(workItem.getStatus().getId());
        return workItemDTO;
    }

    private void DTOtoObject(WorkItemDTO workItemDTO, WorkItem workItem) {
        if (workItemDTO.getTitle() != null) {
            workItem.setTitle(workItemDTO.getTitle());
        }
        if (workItemDTO.getDescription() != null) {
            workItem.setDescription(workItemDTO.getDescription());
        }
        if (workItemDTO.getReviewerId() > 0) {
            User reviewer = userRepository.getById(workItemDTO.getReviewerId());
            workItem.setReviewer(reviewer);
        }
        if (workItemDTO.getStatusId() > 0) {
            ItemStatus status = statusService.getById(workItemDTO.getStatusId());
            workItem.setStatus(status);
        } else {
            if (workItem.getStatus() == null) {
                ItemStatus status = itemStatusRepository.getByField("status", "Pending");
                workItem.setStatus(status);
            }
        }
        if (workItem.getCreator() == null) {
            if (workItemDTO.getCreatorId() > 0) {
                User creator = userRepository.getById(workItemDTO.getCreatorId());
                workItem.setCreator(creator);
                Team team = teamService.getById(creator.getTeam().getId());
                workItem.setTeam(team);
            }
        }
    }
}