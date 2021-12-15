package com.telerikacademy.finalprojectpeerreview.services;

import com.telerikacademy.finalprojectpeerreview.TestHelpers;
import com.telerikacademy.finalprojectpeerreview.exceptions.EntityNotFoundException;
import com.telerikacademy.finalprojectpeerreview.models.Invitation;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.repositories.contracts.InvitationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InvitationServiceImplTests {

    @Mock
    InvitationRepository invitationRepository;

    @InjectMocks
    InvitationServiceImpl invitationService;

    /*
        @Test
        public void create_shouldThrow_when_identicalInvitationExists() {
            //Arrange
            Invitation mockInvitation = TestHelpers.createMockInvitation();

            Mockito.when(invitationRepository.getAll()).thenReturn(List.of(mockInvitation));

            //Act, Assert
            Assertions.assertThrows(DuplicateEntityException.class, () ->
                    invitationService.update(mockInvitation, mockInvitation.getCreator()));
        }
    */
   /* @Test
    public void update_should_callInvitationRepository() throws DuplicateEntityException {
        //Arrange
        Invitation invitation = TestHelpers.createMockInvitation();
        User user = invitation.getCreator();
        user.setTeam(null);

        //Act
        invitationService.update(invitation, invitation.getCreator());

        //Assert
        Mockito.verify(invitationRepository, Mockito.times(1))
                .update(invitation);
    }*/

    @Test
    public void delete_should_callRepository() throws EntityNotFoundException {
        //Arrange
        User user = TestHelpers.createMockStandardUser();
        Invitation invitationMock = TestHelpers.createMockInvitation();
        invitationMock.setDelete(1);
        Mockito.when(invitationRepository.getById(Mockito.anyInt())).thenReturn(invitationMock);

        //Act
        invitationService.delete(invitationMock.getId(), user);

        //Assert
        Mockito.verify(invitationRepository, Mockito.times(1))
                .update((invitationMock));
    }
}
