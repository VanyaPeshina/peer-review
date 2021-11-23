package com.telerikacademy.finalprojectpeerreview.models.mappers;

import com.telerikacademy.finalprojectpeerreview.models.DTOs.UserDTO;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.UserRole;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.TeamRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRoleRepository;
import com.telerikacademy.finalprojectpeerreview.utils.FileConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.telerikacademy.finalprojectpeerreview.utils.constants.*;

@Component
public class UserMapper {

    private final UserRepository userRepository;
    private final FileConverter fileConverter;
    private final TeamRepository teamRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserMapper(UserRepository userRepository, FileConverter fileConverter, TeamRepository teamRepository,
                      UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.fileConverter = fileConverter;
        this.teamRepository = teamRepository;

        this.userRoleRepository = userRoleRepository;
    }

    public User fromDto(UserDTO userDTO) throws IOException {
        User user;
        if (userDTO.getId() == 0) {
            user = new User();
        } else {
            user = userRepository.getById(userDTO.getId());
        }
        DTOtoObject(userDTO, user);
        return user;
    }

    private void DTOtoObject(UserDTO userDTO, User user) throws IOException {
        if (userDTO.getUsername() != null) {
            if (userRepository.getAll().stream().anyMatch(user1 -> user1.getUsername().equals(userDTO.getUsername()))) {
                throw new IllegalArgumentException(USERNAME_SHOULD_BE_UNIQUE);
            }
            user.setUsername(userDTO.getUsername());
        }
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
                user.setPassword(userDTO.getPassword());
            } else {
                throw new IllegalArgumentException(PASSWORD_SHOULD_CONTAIN);
            }
        }
        if (userDTO.getEmail() != null) {
            if (userRepository.getAll().stream().anyMatch(user1 -> user1.getEmail().equals(userDTO.getEmail()))) {
                throw new IllegalArgumentException(EMAIL_SHOULD_BE_UNIQUE);
            }
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPhone() != null) {
            if (userRepository.getAll().stream().anyMatch(user1 -> user1.getPhone().equals(userDTO.getPhone()))) {
                throw new IllegalArgumentException(PHONE_SHOULD_BE_UNIQUE);
            }
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getPhoto() != null) {
            byte[] imageInBytes = fileConverter.convertToBytes(userDTO.getPhoto());
            user.setPhoto(imageInBytes);
        }
        if (userDTO.getTeamId() > 0) {
            Team team = teamRepository.getById(userDTO.getTeamId());
            user.setTeam(team);
        }
        UserRole userRole = userRoleRepository.getByField("role", "User");
        user.setRole(userRole);
    }
}
