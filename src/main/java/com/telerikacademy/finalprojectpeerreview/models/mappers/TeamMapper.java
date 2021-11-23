package com.telerikacademy.finalprojectpeerreview.models.mappers;

import com.telerikacademy.finalprojectpeerreview.models.DTOs.TeamDTO;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.TeamRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.telerikacademy.finalprojectpeerreview.utils.constants.*;

@Component
public class TeamMapper {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Autowired
    public TeamMapper(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public Team fromDto(TeamDTO teamDTO){
        Team team;
        if (teamDTO.getId() == 0) {
            team = new Team();
        } else {
            team = teamRepository.getById(teamDTO.getId());
        }
        DTOtoObject(teamDTO, team);
        return team;
    }

    private void DTOtoObject(TeamDTO teamDTO, Team team){
        if (teamDTO.getName() != null) {
            if (teamRepository.getAll().stream().anyMatch(team1 -> team1.getName().equals(teamDTO.getName()))) {
                throw new IllegalArgumentException(TEAM_NAME_SHOULD_BE_UNIQUE);
            }
            team.setName(teamDTO.getName());
        }
        if (teamDTO.getOwnerId() > 0) {
            User owner = userRepository.getById(teamDTO.getOwnerId());
            team.setOwner(owner);
        }
    }
}
