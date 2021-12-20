package com.telerikacademy.finalprojectpeerreview.exceptions;

public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException(String reason) {
        super(reason);
    }
}
