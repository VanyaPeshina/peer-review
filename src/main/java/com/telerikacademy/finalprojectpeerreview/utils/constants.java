package com.telerikacademy.finalprojectpeerreview.utils;

public class constants {
    //DTOs and Models
    public final static String PROPERTY_CANT_BE_EMPTY = "Property can't be empty";
    public final static String EMAIL_SHOULD_BE_VALID = "Email should be valid";
    public final static String EMAIL_SHOULD_BE_UNIQUE = "A user with this e-mail is already registered";
    public final static String USERNAME_SHOULD_BE_UNIQUE = "A user with this username is already registered";
    public final static String PHONE_SHOULD_BE_UNIQUE = "A user with this phone number is already registered";
    public final static String PHONE_SHOULD_BE_EXACTLY = "Phone number should be 10 digits";
    public final static String NAME_SHOULD_BE_BETWEEN = "Name must be between 2 and 20";
    public final static String TEAM_NAME_SHOULD_BE_BETWEEN = "Name must be between 3 and 30";
    public final static String TEAM_NAME_SHOULD_BE_UNIQUE = "A team with this name is already registered";
    public final static String PASSWORD_SHOULD_BE_BETWEEN = "Password must be at least 8 symbols";
    public final static String PASSWORD_SHOULD_CONTAIN = "Password should contain a capital letter and a special symbol";

    //Authentication and Authorization
    public final static String AUTHORIZATION_HEADER_NAME = "Authorization";
    public final static String MODIFY_ADMIN_ERROR_MESSAGE = "Only Admin can modify this entity.";
    public final static String MODIFY_ERROR_MESSAGE = "Only Admin or Employee can modify this entity.";
    public final static String OWNER_MODIFY_ERROR_MESSAGE = "Only Admin or Owner can modify this entity.";
    public final static String AUTHENTICATION_FAILURE_MESSAGE = "Wrong username or password.";

}
