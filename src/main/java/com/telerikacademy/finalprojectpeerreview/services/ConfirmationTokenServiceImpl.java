package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.repositories.contracts.CRUDRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.ConfirmationTokenRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.ConfirmationTokenService;
import com.telerikacademy.finalprojectpeerreview.models.ConfirmationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenServiceImpl extends CRUDServiceImpl<ConfirmationToken> implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public ConfirmationTokenServiceImpl(CRUDRepository<ConfirmationToken> crudRepository, ConfirmationTokenRepository confirmationTokenRepository) {
        super(crudRepository);
        this.confirmationTokenRepository = confirmationTokenRepository;
    }
}
