package com.telerikacademy.finalprojectpeerreview.models.DTOs;

import javax.validation.constraints.NotNull;

public class InvitationDTO {

    @NotNull
    private int creatorId;

    @NotNull
    private int teamId;

    @NotNull
    private int invitedId;

    public InvitationDTO() {
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getInvitedId() {
        return invitedId;
    }

    public void setInvitedId(int invitedId) {
        this.invitedId = invitedId;
    }
}
