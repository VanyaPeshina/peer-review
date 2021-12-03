package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.WorkItemRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import com.telerikacademy.finalprojectpeerreview.utils.AuthorizationCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.telerikacademy.finalprojectpeerreview.utils.constants.*;

@Service
public class WorkItemServiceImpl extends CRUDServiceImpl<WorkItem> implements WorkItemService {

    private final WorkItemRepository workItemRepository;

    @Autowired
    public WorkItemServiceImpl(WorkItemRepository workItemRepository) {
        super(workItemRepository);
        this.workItemRepository = workItemRepository;
    }

    @Override
    public List<WorkItem> search(Optional<String> search) {
        if (search.get().length() == 0) {
            return workItemRepository.getAll();
        }
        return workItemRepository.search(search.get());
    }

    @Override
    public void create(WorkItem workItem, User user) {
        //TODO -> трябва ли да проверяваме за reviewer при създаването? Той не е задължителен.
//        checkForTeam(workItem);
        if (checkForDuplicates(workItem)) {
            throw new DuplicateEntityException(workItem.getClass().getSimpleName(), "these", "parameters");
        }
        workItemRepository.create(workItem);
    }

    @Override
    public void update(WorkItem workItem, User user) {
        AuthorizationCheck.checkForInvolved(workItem, user);
        checkForTeam(workItem);
        if (checkForDuplicates(workItem)) {
            throw new DuplicateEntityException(workItem.getClass().getSimpleName(), "these", "parameters");
        }
        workItemRepository.update(workItem);
    }

    @Override
    public List<WorkItem> filter(Optional<String> name, Optional<String> status,
                                 Optional<String> reviewer, Optional<String> sort) throws EntityNotFoundException {
        List<WorkItem> filteredItems = workItemRepository.filter(name, status, reviewer, sort);
        if (filteredItems.isEmpty()) {
            throw new EntityNotFoundException("work items", "this", "parameters");
        }
        return filteredItems;
    }

    protected void checkForTeam(WorkItem workItem) {
        if (workItem.getCreator().getTeam().getId() != workItem.getReviewer().getTeam().getId()) {
            throw new IllegalArgumentException(TEAM_MUST_BE_SAME);
        }
    }
}
