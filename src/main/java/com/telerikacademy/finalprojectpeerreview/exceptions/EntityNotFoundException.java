package com.telerikacademy.finalprojectpeerreview.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String type, String name) {
        this(type, "name", name);
    }

    public EntityNotFoundException(String type, int id) {
        this(type, "id", String.valueOf(id));
    }

    public EntityNotFoundException(String type, String attribute, String value) {
        super(String.format("%s with %s %s is not found", type, attribute, value));
    }
}
