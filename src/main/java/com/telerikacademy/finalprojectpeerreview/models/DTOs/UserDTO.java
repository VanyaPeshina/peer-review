package com.telerikacademy.finalprojectpeerreview.models.DTOs;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import static com.telerikacademy.finalprojectpeerreview.utils.constants.*;

public class UserDTO {

    private int id;

    @Size(min = 2, max = 20, message = NAME_SHOULD_BE_BETWEEN)
    private String username;

    @Size(min = 8, message = PASSWORD_SHOULD_BE_AT_LEAST)
    //TODO: Regex to validate password
    private String password;

    private String secondPassword;

    @Email(message = EMAIL_SHOULD_BE_VALID)
    //TODO: Regex to validate email
    private String email;

    @Size(min = 10, max = 10, message = PHONE_SHOULD_BE_EXACTLY)
    private String phone;

    private String photoName;

    private int teamId;

    private MultipartFile multipartFile;

    private int userRole;

    public UserDTO() {
    }

    public UserDTO(int id, String username, String password, String email, String phone, String photoName,
                   int userRole) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.photoName = photoName;
        this.userRole = userRole;
        this.teamId = 0;
    }

    public UserDTO(int id, String username, String password, String email, String phone, String photoName,
                   int teamId, int userRole) {
        this(id, username, password, email, phone, photoName, userRole);
        this.teamId = teamId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public String getSecondPassword() {
        return secondPassword;
    }

    public void setSecondPassword(String secondPassword) {
        this.secondPassword = secondPassword;
    }
}
