package com.telerikacademy.finalprojectpeerreview.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telerikacademy.finalprojectpeerreview.exceptions.MyFileNotFoundException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.UrlResource;

import javax.annotation.Resource;
import javax.persistence.*;
import java.net.MalformedURLException;
import java.nio.file.Path;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_name")
    private String photoName;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private UserRole role;

    @Transient
    private String getPhotoForMVC;

    public User() {
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

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getGetPhotoForMVC() {
        return "/api/users/" + getId() + "/photo";
    }

    public void setGetPhotoForMVC(String getPhotoForMVC) {
        this.getPhotoForMVC = getPhotoForMVC;
    }
}

