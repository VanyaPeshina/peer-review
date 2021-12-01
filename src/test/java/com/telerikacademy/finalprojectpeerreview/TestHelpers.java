package com.telerikacademy.finalprojectpeerreview;

import com.telerikacademy.finalprojectpeerreview.models.*;

public class TestHelpers {

    //user
    public static User createMockStandardUser() {
        return createMockUser("User");
    }

    public static User createMockAdmin() {
        return createMockUser("Admin");
    }

    public static User createMockUser(String role) {
        var mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("MockUser");
        mockUser.setEmail("mockuser@email.com");
        mockUser.setPassword("Passw0rd#");
        mockUser.setPhone("0888888888");
        mockUser.setRole(createMockUserRole(role));
        return mockUser;
    }

    //user role
    public static UserRole createMockUserRole(String role) {
        var mockUserRole = new UserRole();
        mockUserRole.setId(1);
        mockUserRole.setRole(role);
        return mockUserRole;
    }

    //team
    public static Team createMockTeam() {
        var mockTeam = new Team();
        mockTeam.setId(1);
        mockTeam.setName("MockTeam");
        mockTeam.setOwner(createMockStandardUser());
        return mockTeam;
    }

    //work item
    public static WorkItem createMockWorkItem() {
        var mockWorkItem = new WorkItem();
        mockWorkItem.setId(1);
        mockWorkItem.setTitle("Mock Title");
        mockWorkItem.setDescription("Mock description with appropriate length");
        mockWorkItem.setCreator(createMockStandardUser());
        mockWorkItem.setStatus(createMockItemStatus("Pending"));
        return mockWorkItem;
    }

    //item status
    public static ItemStatus createMockItemStatus(String status) {
        var mockItemStatus = new ItemStatus();
        mockItemStatus.setId(1);
        mockItemStatus.setStatus(status);
        return mockItemStatus;
    }

    //comment
    public static Comment createMockComment() {
        var mockComment = new Comment();
        mockComment.setId(1);
        mockComment.setComment("Mock Comment");
        mockComment.setCreator(createMockStandardUser());
        mockComment.setWorkItem(createMockWorkItem());
        return mockComment;
    }
}
