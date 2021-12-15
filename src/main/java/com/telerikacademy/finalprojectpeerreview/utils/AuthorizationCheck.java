package com.telerikacademy.finalprojectpeerreview.utils;

import com.telerikacademy.finalprojectpeerreview.exceptions.UnauthorizedOperationException;
import com.telerikacademy.finalprojectpeerreview.models.User;
import com.telerikacademy.finalprojectpeerreview.models.WorkItem;

import static com.telerikacademy.finalprojectpeerreview.utils.constants.*;

public class AuthorizationCheck {

    public static void checkForAdmin(User user) throws UnauthorizedOperationException {
        if (!user.getRole().getRole().equals("Admin")) {
            throw new UnauthorizedOperationException(MODIFY_ADMIN_ERROR_MESSAGE);
        }
    }

    public static void checkForInvolved(WorkItem workItem, User userToAuthenticate) throws UnauthorizedOperationException {
        if (!userToAuthenticate.getRole().getRole().equals("Admin")
                && !workItem.getCreator().getUsername().equals(userToAuthenticate.getUsername())
                && !workItem.getReviewer().getUsername().equals(userToAuthenticate.getUsername())) {
            throw new UnauthorizedOperationException(INVOLVED_MODIFY_ERROR_MESSAGE);
        }
    }
}
