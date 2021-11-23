package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.models.ItemStatus;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.ItemStatusRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.ItemStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemStatusServiceImpl extends CRUDServiceImpl<ItemStatus> implements ItemStatusService {

    private ItemStatusRepository itemStatusRepository;

    @Autowired
    public ItemStatusServiceImpl(ItemStatusRepository itemStatusRepository) {
        super(itemStatusRepository);
        this.itemStatusRepository = itemStatusRepository;
    }
}
