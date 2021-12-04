package com.telerikacademy.finalprojectpeerreview.repositories.contracts;

import com.telerikacademy.finalprojectpeerreview.models.WorkItem;

import java.util.List;
import java.util.Optional;

public interface WorkItemRepository extends CRUDRepository<WorkItem> {

    List<WorkItem> search(String search);

    List<WorkItem> filter(Optional<String> name, Optional<String> status,
                          Optional<String> reviewer, Optional<String> sort);

    List<WorkItem> filterMVC(Optional<Integer> creatorId,
                             Optional<Integer> reviewerId,
                             Optional<Integer> statusId);
}
