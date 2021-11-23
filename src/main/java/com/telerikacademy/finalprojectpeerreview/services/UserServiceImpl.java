package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.repositories.UserRepositoryImpl;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends CRUDServiceImpl<User> implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    @Override
    public List<User> search(Optional<String> search) {
        if (search.get().length() == 0) {
            return userRepository.getAll();
        }
        return userRepository.search(search.get());
    }

   /* @Override
    public User getByUsername(String username) {
        return userRepositoryImpl.getByUsername(username);
    }*/
}
