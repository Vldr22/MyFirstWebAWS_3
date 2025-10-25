package org.education.firstwebproject.model;

public enum UserRole {

    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER_ADDED("ROLE_USER_ADDED");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    public String getRoleName() {
        return authority.substring(5);
    }

    @Override
    public String toString() {
        return authority;
    }
}
