package org.education.firstwebproject.model.enums;

import lombok.Getter;

/**
 * Роли пользователей в системе.
 */
@Getter
public enum UserRole {

    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER_ADDED("ROLE_USER_ADDED");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    /**
     * Преобразует строку в enum. Возвращает ROLE_USER если роль не найдена.
     */
    public static UserRole fromString(String authority) {
        for (UserRole role : values()) {
            if (role.authority.equals(authority)) {
                return role;
            }
        }
        return ROLE_USER;
    }

    public String getRoleName() {
        return authority.substring(5);
    }

    @Override
    public String toString() {
        return authority;
    }
}
