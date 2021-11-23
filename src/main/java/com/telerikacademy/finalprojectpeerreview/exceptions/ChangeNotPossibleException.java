package com.telerikacademy.finalprojectpeerreview.exceptions;

public class ChangeNotPossibleException extends RuntimeException {

    public ChangeNotPossibleException(String entity, String reason) {
        super(String.format("The change of this %s is not possible because %s.", entity, reason));
    }
}
