package com.telerikacademy.finalprojectpeerreview.security.enums;

import org.springframework.security.core.GrantedAuthority;

public enum ApplicationUserPermission implements GrantedAuthority {
    SIMPLE_USER_READ("simple_user:read"),
    SIMPLE_USER_WRITE("simple_user:write"),
    WORK_ITEM_READ("work_item:read"),
    WORK_ITEM_WRITE("work_item:write");

        private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public String getAuthority() {
        return permission;
    }
}
