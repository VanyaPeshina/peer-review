package com.telerikacademy.finalprojectpeerreview.utils;

public class constants {
    //DTOs and Models
    public final static String PROPERTY_CANT_BE_EMPTY = "Property can't be empty";
    public final static String EMAIL_SHOULD_BE_VALID = "Email should be valid";
    public final static String EMAIL_SHOULD_BE_UNIQUE = "A user with this e-mail is already registered";
    public final static String USERNAME_SHOULD_BE_UNIQUE = "A user with this username is already registered";
    public final static String PHONE_SHOULD_BE_UNIQUE = "A user with this phone number is already registered";
    public final static String PHONE_SHOULD_BE_EXACTLY = "Phone number should be 10 digits";
    public final static String NAME_SHOULD_BE_BETWEEN = "Name must be between 2 and 20 characters";
    public final static String TEAM_NAME_SHOULD_BE_BETWEEN = "Name must be between 3 and 30 characters";
    public final static String TEAM_NAME_SHOULD_BE_UNIQUE = "A team with this name is already registered";
    public final static String ITEM_TITLE_SHOULD_BE_BETWEEN = "Name must be between 10 and 80 characters";
    public final static String DESCRIPTION_SHOULD_BE_BETWEEN = "Name must be between 20 and 65535 characters";
    public final static String PASSWORD_SHOULD_BE_AT_LEAST = "Password should be at least 8 symbols";
    public final static String PASSWORD_SHOULD_CONTAIN = "Password should contain a capital letter and a special symbol";

    //Authentication and Authorization
    public final static String AUTHORIZATION_HEADER_NAME = "Authorization";
    public final static String MODIFY_ADMIN_ERROR_MESSAGE = "Only Admin can modify this entity.";
    public final static String INVOLVED_MODIFY_ERROR_MESSAGE = "Only Admin or Involved user can modify this entity.";
    public final static String AUTHENTICATION_FAILURE_MESSAGE = "Wrong username or password.";

    //Business logic
    public final static String TEAM_MUST_BE_SAME = "Reviewer must be part of the same team as the creator";

}
