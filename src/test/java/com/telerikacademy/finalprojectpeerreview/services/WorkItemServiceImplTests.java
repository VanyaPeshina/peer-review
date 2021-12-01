package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.WorkItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.telerikacademy.finalprojectpeerreview.TestHelpers.*;

@ExtendWith(MockitoExtension.class)
public class WorkItemServiceImplTests {

    @Mock
    WorkItemRepository workItemRepository;

    @InjectMocks
    WorkItemServiceImpl workItemService;

    @Test
    public void search_should_call_Repository() {
        //Arrange
        Optional<String> searchParameter = Optional.of("search");
        Mockito.when(workItemRepository.search(Mockito.anyString())).thenReturn(new ArrayList<>());

        //Act
        workItemService.search(searchParameter);

        //Assert
        Mockito.verify(workItemRepository, Mockito.times(1)).search("search");
    }

    @Test
    public void create_shouldThrow_whenTeamOfTheWorkItem_isDifferent_from_teamOfReviewer() {
        //Arrange
        WorkItem workItem = createMockWorkItem();
        Team team = createMockTeam();
        Team team2 = createMockTeam();
        team2.setId(2);
        workItem.setTeam(team);
        workItem.getCreator().setTeam(team);
        User reviewer = createMockStandardUser();
        reviewer.setId(2);
        reviewer.setTeam(team2);
        workItem.setReviewer(reviewer);

        //Act, Assert
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> workItemService.create(workItem, workItem.getCreator()));
    }

    @Test
    public void create_should_callRepository() {
        //Arrange
        WorkItem workItem = createMockWorkItem();
        Team team = createMockTeam();
        workItem.setTeam(team);
        workItem.getCreator().setTeam(team);
        User reviewer = createMockStandardUser();
        reviewer.setId(2);
        reviewer.setTeam(team);
        workItem.setReviewer(reviewer);

        //Act
        workItemService.create(workItem, createMockStandardUser());

        //Assert
        Mockito.verify(workItemRepository, Mockito.times(1)).create(workItem);
    }

    @Test
    public void update_should_callRepository() {
        //Arrange
        WorkItem workItem = createMockWorkItem();
        Team team = createMockTeam();
        workItem.setTeam(team);
        workItem.getCreator().setTeam(team);
        User reviewer = createMockStandardUser();
        reviewer.setId(2);
        reviewer.setTeam(team);
        workItem.setReviewer(reviewer);

        //Act
        workItemService.update(workItem, workItem.getCreator());

        //Assert
        Mockito.verify(workItemRepository, Mockito.times(1)).update(workItem);
    }

    @Test
    public void filter_should_call_Repository() {
        //Arrange
        WorkItem workItem = createMockWorkItem();
        Optional<String> filterParameter = Optional.of("filter");
        Optional<String> filterParameter2 = Optional.of("filter");
        Optional<String> filterParameter3 = Optional.of("filter");
        Optional<String> filterParameter4 = Optional.of("filter");

        Mockito.when(workItemRepository.filter(filterParameter, filterParameter2, filterParameter3, filterParameter4))
                .thenReturn(List.of(workItem));

        //Act
        workItemService.filter(filterParameter, filterParameter2, filterParameter3, filterParameter4);

        //Assert
        Mockito.verify(workItemRepository, Mockito.times(1))
                .filter(filterParameter, filterParameter2, filterParameter3, filterParameter4);
    }
}
