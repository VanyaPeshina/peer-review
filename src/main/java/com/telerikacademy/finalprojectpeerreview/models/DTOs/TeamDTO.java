package com.telerikacademy.finalprojectpeerreview.models.DTOs;

import javax.validation.constraints.Size;

import static com.telerikacademy.finalprojectpeerreview.utils.constants.*;

public class TeamDTO {

    private int id;

    @Size(min = 3, max = 30, message = TEAM_NAME_SHOULD_BE_BETWEEN)
    private String name;

    private int ownerId;

    public TeamDTO(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
