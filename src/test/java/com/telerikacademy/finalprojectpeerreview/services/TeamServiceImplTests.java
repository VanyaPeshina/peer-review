package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static com.telerikacademy.finalprojectpeerreview.TestHelpers.createMockStandardUser;
import static com.telerikacademy.finalprojectpeerreview.TestHelpers.createMockTeam;

@ExtendWith(MockitoExtension.class)
public class TeamServiceImplTests {

    @Mock
    TeamRepository repository;

    @InjectMocks
    TeamServiceImpl teamService;

    @Test
    public void getMembers_should_call_Repository() {
        //Arrange
        Team team = createMockTeam();
        User user = createMockStandardUser();
        Mockito.when(repository.getMembers(team)).thenReturn(new ArrayList<>());

        //Act
        teamService.getMembers(team, user);

        //Assert
        Mockito.verify(repository, Mockito.times(1)).getMembers(team);
    }

    @Test
    public void getWorkItems_should_call_Repository() {
        //Arrange
        Team team = createMockTeam();
        Mockito.when(repository.getTeamWorkItems(team)).thenReturn(new ArrayList<>());

        //Act
        teamService.getTeamWorkItems(team);

        //Assert
        Mockito.verify(repository, Mockito.times(1)).getTeamWorkItems(team);
    }
}
