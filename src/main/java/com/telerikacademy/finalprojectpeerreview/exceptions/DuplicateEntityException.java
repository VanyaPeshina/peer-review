package com.telerikacademy.finalprojectpeerreview.exceptions;

public class DuplicateEntityException extends Exception {

    public DuplicateEntityException(String type, String attribute, String value) {
        super(String.format("%s with %s %s already exists.", type, attribute, value));
    }
}
