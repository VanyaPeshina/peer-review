package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
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
public class UserServiceImplTests {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void search_should_call_Repository() {
        //Arrange
        Optional<String> searchParameter = Optional.of("search");
        Mockito.when(userRepository.search(Mockito.anyString())).thenReturn(new ArrayList<>());

        //Act
        userService.search(searchParameter);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1)).search("search");
    }

    @Test
    public void getAllRequests_shouldThrow_when_ListIsEmpty() {
        //Arrange
        User user = createMockStandardUser();
        Mockito.when(userRepository.getAllRequests(Mockito.anyInt())).thenReturn(new ArrayList<>());

        //Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getAllRequests(user.getId()));
    }

    @Test
    public void getAllRequests_should_call_Repository() {
        //Arrange
        User user = createMockStandardUser();
        WorkItem workItem = createMockWorkItem();
        Mockito.when(userRepository.getAllRequests(Mockito.anyInt())).thenReturn(List.of(workItem));

        //Act
        userService.getAllRequests(user.getId());

        //Assert
        Mockito.verify(userRepository, Mockito.times(1)).getAllRequests(user.getId());
    }
}
