package com.telerikacademy.finalprojectpeerreview.models.DTOs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.telerikacademy.finalprojectpeerreview.utils.constants.*;

public class LoginDto {

    @NotBlank(message = PROPERTY_CANT_BE_EMPTY)
    private String username;

    @NotBlank(message = PROPERTY_CANT_BE_EMPTY)
    @Size(min = 2, max = 20, message = EMAIL_SHOULD_BE_VALID)
    private String password;

    public LoginDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
