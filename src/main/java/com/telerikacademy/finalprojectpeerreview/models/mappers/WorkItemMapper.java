package com.telerikacademy.finalprojectpeerreview.models.mappers;

import com.telerikacademy.finalprojectpeerreview.models.DTOs.WorkItemDTO;
import com.telerikacademy.finalprojectpeerreview.models.ItemStatus;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.WorkItemRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.ItemStatusService;
import com.telerikacademy.finalprojectpeerreview.utils.FileConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.sql.SQLException;

@Component
public class WorkItemMapper {

    private final WorkItemRepository workItemRepository;
    private final UserRepository userRepository;
    private final ItemStatusService statusService;
    private final FileConverter fileConverter;

    @Autowired
    public WorkItemMapper(WorkItemRepository workItemRepository, UserRepository userRepository,
                          ItemStatusService statusService, FileConverter fileConverter) {
        this.workItemRepository = workItemRepository;
        this.userRepository = userRepository;
        this.statusService = statusService;
        this.fileConverter = fileConverter;
    }

    public WorkItem fromDto(WorkItemDTO workItemDTO) throws SQLException, FileNotFoundException {
        WorkItem workItem;
        if (workItemDTO.getId() == 0) {
            workItem = new WorkItem();
        } else {
            workItem = workItemRepository.getById(workItemDTO.getId());
        }
        DTOtoObject(workItemDTO, workItem);
        return workItem;
    }

    private void DTOtoObject(WorkItemDTO workItemDTO, WorkItem workItem) throws SQLException, FileNotFoundException {
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
        }
        if (workItemDTO.getCreatorId() > 0) {
            User creator = userRepository.getById(workItemDTO.getCreatorId());
            workItem.setCreator(creator);
        }
        if (workItemDTO.getWork_item() != null) {
            workItem.setWorkItem(workItemDTO.getWork_item().getBytes());
        }
    }
}
