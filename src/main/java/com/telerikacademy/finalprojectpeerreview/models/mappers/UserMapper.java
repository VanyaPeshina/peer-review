package com.telerikacademy.finalprojectpeerreview.models.mappers;

import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.WrongPasswordException;
import com.telerikacademy.finalprojectpeerreview.models.DTOs.UserDTO;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.UserRole;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.TeamRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.telerikacademy.finalprojectpeerreview.utils.constants.*;

@Component
public class UserMapper {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserMapper(UserRepository userRepository,
                      TeamRepository teamRepository,
                      UserRoleRepository userRoleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User fromDto(UserDTO userDTO) throws EntityNotFoundException {
        User user;
        if (userDTO.getId() == 0) {
            user = new User();
        } else {
            user = userRepository.getById(userDTO.getId());
        }
        DTOtoObject(userDTO, user);
        return user;
    }

    private void DTOtoObject(UserDTO userDTO, User user) throws EntityNotFoundException {
        doUsername(userDTO, user);
        doPassword(userDTO, user);
        doEmail(userDTO, user);
        doPhone(userDTO, user);
        doPhotoName(userDTO, user);
        doTeam(userDTO, user);
        doUserRole(userDTO, user);
        doSecondPassword(userDTO, user);
        setNewUser(userDTO, user);
    }

    public UserDTO toDto(User user) {
        if (user.getTeam() != null) {
            return new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(),
                    user.getPhone(), user.getPhotoName(), user.getTeam().getId(), user.getRole().getId());
        }
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(),
                user.getPhone(), user.getPhotoName(), user.getRole().getId());
    }

    private void doUserRole(UserDTO userDTO, User user) throws EntityNotFoundException {
        UserRole userRole;
        if (userDTO.getUserRole() > 0 && user.getRole() != null) {
            userRole = userRoleRepository.getByField("id", userDTO.getUserRole());
            user.setRole(userRole);
        } else if (user.getRole() == null) {
            userRole = userRoleRepository.getByField("role", "User");
            user.setRole(userRole);
        }
    }

    private void doTeam(UserDTO userDTO, User user) throws EntityNotFoundException {
        if (userDTO.getTeamId() > 0) {
            Team team = teamRepository.getById(userDTO.getTeamId());
            user.setTeam(team);
        }
    }

    private void doPhotoName(UserDTO userDTO, User user) {
        if (userDTO.getPhotoName() != null) {
            user.setPhotoName(userDTO.getPhotoName());
        } else if (user.getPhotoName() == null) {
            user.setPhotoName("avatardefault.png");
        }
    }

    private void doPhone(UserDTO userDTO, User user) {
        if (userDTO.getPhone() != null && !userDTO.getPhone().equals(user.getPhone())) {
            if (userRepository.getAll().stream().anyMatch(user1 -> user1.getPhone().equals(userDTO.getPhone()))) {
                throw new IllegalArgumentException(PHONE_SHOULD_BE_UNIQUE);
            }
            user.setPhone(userDTO.getPhone());
        }
    }

    private void doEmail(UserDTO userDTO, User user) {
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.getAll().stream().anyMatch(user1 -> user1.getEmail().equals(userDTO.getEmail()))) {
                throw new IllegalArgumentException(EMAIL_SHOULD_BE_UNIQUE);
            }
            user.setEmail(userDTO.getEmail());
        }
    }

    private void doUsername(UserDTO userDTO, User user) {
        if (userDTO.getUsername() != null) {
            if (userRepository.getAll().stream().anyMatch(user1 -> user1.getUsername().equals(userDTO.getUsername()))) {
                throw new IllegalArgumentException(USERNAME_SHOULD_BE_UNIQUE);
            }
            user.setUsername(userDTO.getUsername());
        }
    }

    private void doPassword(UserDTO userDTO, User user) {
        if (userDTO.getPassword() != null) {
            //check for capital letter
            boolean capitalLetter = false;
            for (char symbol : userDTO.getPassword().toCharArray()) {
                if (symbol >= 65 && symbol <= 90) {
                    capitalLetter = true;
                    break;
                }
            }
            //check for special symbol
            boolean specialSymbol = false;
            for (char symbol : userDTO.getPassword().toCharArray()) {
                if ((symbol >= 33 && symbol <= 47) || (symbol >= 58 && symbol <= 64)) {
                    specialSymbol = true;
                    break;
                }
            }
            if (capitalLetter && specialSymbol) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            } else {
                throw new IllegalArgumentException(PASSWORD_SHOULD_CONTAIN);
            }
        }
    }

    private void doSecondPassword(UserDTO userDTO, User user) {
        if (userDTO.getSecondPassword() != null) {
            if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                throw new WrongPasswordException("Wrong password provided");
            }
            userDTO.setPassword(userDTO.getSecondPassword());
            doPassword(userDTO, user);
        }
    }

    private void setNewUser(UserDTO userDTO, User user) {
        if (userDTO.getId() == 0) {
            user.setDelete(0);
            user.setEnabled(false);
            user.setLocked(false);
        }
    }
}
