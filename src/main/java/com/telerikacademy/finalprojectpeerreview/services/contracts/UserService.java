package com.telerikacademy.finalprojectpeerreview.services.contracts;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.ConfirmationToken;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService extends CRUDService<User>, UserDetailsService {

    List<User> search(Optional<String> search) throws EntityNotFoundException;

    List<WorkItem> getAllRequests(int id);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    void signUpUser(User user, ConfirmationToken confirmationToken) throws EntityNotFoundException;

    void confirmToken(String token) throws EntityNotFoundException, DuplicateEntityException, UnauthorizedOperationException;
}
