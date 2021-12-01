package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.exceptions.DuplicateEntityException;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.CRUDRepository;
import com.telerikacademy.finalprojectpeerreview.services.CRUDServiceImpl;
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
import static com.telerikacademy.finalprojectpeerreview.TestHelpers.createMockWorkItem;

@ExtendWith(MockitoExtension.class)
public class crudServiceImplTests<E> {

    @Mock
    CRUDRepository<E> repository;

    @InjectMocks
    CRUDServiceImpl<E> service;

    @Test
    public void getAll_should_callRepository() {
        //Arrange
        Mockito.when(repository.getAll()).thenReturn(new ArrayList<>());

        //Act
        service.getAll();

        //Assert
        Mockito.verify(repository, Mockito.times(1)).getAll();
    }

    @Test
    public void getById_should_returnUser_when_matchExists() {
        //Arrange
        User user = createMockStandardUser();
        Mockito.when(repository.getById(Mockito.anyInt())).thenReturn((E) user);

        //Act
        User result = (User) service.getById(user.getId());

        //Assert
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getId(), result.getId()),
                () -> Assertions.assertEquals(user.getUsername(), result.getUsername()),
                () -> Assertions.assertEquals(user.getEmail(), result.getEmail()),
                () -> Assertions.assertEquals(user.getPhone(), result.getPhone()),
                () -> Assertions.assertEquals(user.getRole(), result.getRole()),
                () -> Assertions.assertEquals(user.getPassword(), result.getPassword())
        );
    }

    @Test
    public void create_shouldThrow_when_identicalUserExists() {
        //Arrange
        User mockUser = createMockStandardUser();

        Mockito.when(repository.getAll()).thenReturn((List<E>) List.of(mockUser));

        //Act, Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> service.create((E) mockUser, mockUser));
    }

    @Test
    public void create_should_callRepository() {
        //Arrange
        User user = createMockStandardUser();

        Mockito.when(repository.getAll()).thenThrow(EntityNotFoundException.class);

        //Act
        service.create((E) user, user);

        //Assert
        Mockito.verify(repository, Mockito.times(1)).create((E) user);
    }

    @Test
    public void update_shouldThrow_when_identicalEntityExists() {
        //Arrange
        User user = createMockStandardUser();

        Mockito.when(repository.getAll()).thenReturn((List<E>) List.of(user));

        //Act, Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> service.update((E) user, user));
    }

    @Test
    public void update_should_callRepository() {
        //Arrange
        User user = createMockStandardUser();

        Mockito.when(repository.getAll()).thenThrow(EntityNotFoundException.class);

        //Act
        service.update((E) user, user);

        //Assert
        Mockito.verify(repository, Mockito.times(1)).update((E) user);
    }

    @Test
    public void delete_should_callRepository() {
        //Arrange
        WorkItem workItem = createMockWorkItem();
        User user = createMockStandardUser();

        Mockito.when(repository.getById(Mockito.anyInt())).thenReturn((E) workItem);

        //Act
        service.delete(workItem.getId(), user);

        //Assert
        Mockito.verify(repository, Mockito.times(1)).delete((E) workItem);
    }
}
