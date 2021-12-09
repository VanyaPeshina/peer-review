package com.telerikacademy.finalprojectpeerreview.security.enums;

import com.google.common.collect.Sets;

import java.util.Set;

import static com.telerikacademy.finalprojectpeerreview.security.enums.ApplicationUserPermission.*;

public enum ApplicationUserRole {
    SIMPLE_USER(Sets.newHashSet(WORK_ITEM_READ, WORK_ITEM_WRITE)),
    ADMIN(Sets.newHashSet(SIMPLE_USER_READ, SIMPLE_USER_WRITE, WORK_ITEM_READ, WORK_ITEM_WRITE));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }
}
