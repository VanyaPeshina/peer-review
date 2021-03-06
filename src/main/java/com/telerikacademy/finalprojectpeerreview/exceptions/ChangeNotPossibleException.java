package com.telerikacademy.finalprojectpeerreview.exceptions;

public class ChangeNotPossibleException extends Exception {

    public ChangeNotPossibleException(String entity, String reason) {
        super(String.format("The change of this %s is not possible because %s.", entity, reason));
    }
}
