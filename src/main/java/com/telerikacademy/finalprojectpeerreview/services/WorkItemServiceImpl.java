package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.WorkItemRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkItemServiceImpl extends CRUDServiceImpl<WorkItem> implements WorkItemService {

    private final WorkItemRepository workItemRepository;

    @Autowired
    public WorkItemServiceImpl(WorkItemRepository workItemRepository) {
        super(workItemRepository);
        this.workItemRepository = workItemRepository;
    }
}
