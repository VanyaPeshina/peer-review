package com.telerikacademy.finalprojectpeerreview.services.contracts;

import com.telerikacademy.finalprojectpeerreview.models.WorkItem;

import java.util.List;
import java.util.Optional;

public interface WorkItemService extends CRUDService<WorkItem> {

    List<WorkItem> search(Optional<String> search);

    List<WorkItem> filter(Optional<String> name, Optional<String> status,
                          Optional<String> reviewer, Optional<String> sort);
}
