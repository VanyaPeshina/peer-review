package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.TestHelpers;
import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.ConfirmationToken;
import com.telerikacademy.finalprojectpeerreview.models.Team;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.UserRepository;
import com.telerikacademy.finalprojectpeerreview.services.contracts.ConfirmationTokenService;
import com.telerikacademy.finalprojectpeerreview.services.contracts.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.telerikacademy.finalprojectpeerreview.TestHelpers.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    UserRepository userRepository;

    @Mock
    ConfirmationTokenService confirmationTokenService;

    @Mock
    UserService mockUserService;

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

 /*   @Test
    public void getAllRequests_shouldThrow_when_ListIsEmpty() {
        //Arrange
        User user = createMockStandardUser();
        Mockito.when(userRepository.getAllRequests(Mockito.anyInt())).thenReturn(new ArrayList<>());

        //Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getAllRequests(user.getId()));
    }
*/
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

    @Test
    public void delete_should_callRepository() throws EntityNotFoundException {
        //Arrange
        User userMock = TestHelpers.createMockStandardUser();
        userMock.setDelete(1);
        Mockito.when(userRepository.getById(Mockito.anyInt())).thenReturn(userMock);

        //Act
        userService.delete(userMock.getId(), userMock);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .update((userMock));
    }

    //TODO needs corrections
    @Test
    public void confirmToken_shouldThrow_when_EmailAlreadyConfirm() throws EntityNotFoundException {
        //Arrange
        ConfirmationToken confirmationToken = TestHelpers.ConfirmationToken();

        confirmationToken.setConfirmedAt(LocalDateTime.now());

        Mockito.when(confirmationTokenService.getByField(
                        Mockito.anyString(), Mockito.anyString()))
                .thenReturn(confirmationToken);

        //Act,Assert
        Assertions.assertThrows(IllegalStateException.class, ()
                -> userService.confirmToken(confirmationToken.getToken()));

    }

    @Test
    public void confirmToken_shouldThrow_when_ExpiredDate() throws EntityNotFoundException {
        //Arrange
        ConfirmationToken confirmationTokenExpired = TestHelpers.ConfirmationTokenExpired();

        Mockito.when(confirmationTokenService.getByField(
                        Mockito.anyString(), Mockito.anyString()))
                .thenReturn(confirmationTokenExpired);

        //Act,Assert
        Assertions.assertThrows(IllegalStateException.class, ()
                -> userService.confirmToken(confirmationTokenExpired.getToken()));

    }

    @Test
    public void updateToken_should_callConfirmationTokenService()
            throws DuplicateEntityException, UnauthorizedOperationException {
        //Arrange
        ConfirmationToken confirmationToken = TestHelpers.ConfirmationToken();
        confirmationToken.setConfirmedAt(LocalDateTime.now());

        //Act
       userService.updateToken(confirmationToken);

        //Assert
        Mockito.verify(confirmationTokenService, Mockito.times(1))
                .update(confirmationToken,confirmationToken.getUser());
    }

    @Test
    public void updateUser_should_callUserRepository() {
        //Arrange
        ConfirmationToken confirmationToken = TestHelpers.ConfirmationToken();
        confirmationToken.setConfirmedAt(LocalDateTime.now());

        //Act
        userService.updateUser(confirmationToken);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .update(confirmationToken.getUser());
    }
}
