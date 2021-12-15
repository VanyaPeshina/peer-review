package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.TestHelpers;
import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.TeamRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.WorkItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.telerikacademy.finalprojectpeerreview.TestHelpers.createMockStandardUser;
import static com.telerikacademy.finalprojectpeerreview.TestHelpers.createMockTeam;

@ExtendWith(MockitoExtension.class)
public class TeamServiceImplTests {

    @Mock
    TeamRepository repository;

    @Mock
    WorkItemService workItemService;

    @InjectMocks
    TeamServiceImpl teamService;

    //////
    @Test
    public void create_shouldThrow_when_identicalTeamExists() {
        //Arrange
        User mockUser = TestHelpers.createMockStandardUser();
        Team mockTeam = TestHelpers.createMockTeam();

        Mockito.when(repository.getAll()).thenReturn(List.of(mockTeam));

        //Act, Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> teamService.create(mockTeam, mockUser));
    }

    @Test
    public void create_should_callRepository() throws DuplicateEntityException {
        //Arrange
        User mockUser = TestHelpers.createMockStandardUser();
        Team mockTeam = TestHelpers.createMockTeam();

        //Act
        teamService.create(mockTeam, mockUser);

        //Assert
        Mockito.verify(repository, Mockito.times(1)).create(mockTeam);
    }

    @Test
    public void delete_should_callRepository() throws EntityNotFoundException {
        //Arrange
        User user = TestHelpers.createMockStandardUser();
        Team teamMock = TestHelpers.createMockTeam();
        teamMock.setDelete(1);
        Mockito.when(repository.getById(Mockito.anyInt())).thenReturn(teamMock);

        //Act
        teamService.delete(teamMock.getId(),user);

        //Assert
        Mockito.verify(repository, Mockito.times(1))
                .update((teamMock));
    }
    /////

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
