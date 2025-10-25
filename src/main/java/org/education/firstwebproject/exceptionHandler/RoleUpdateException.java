package org.education.firstwebproject.exceptionHandler;

public class RoleUpdateException extends RuntimeException {
    public RoleUpdateException(String username) {
        super("Роль не обновилась у пользователя: " + username);
    }

    public RoleUpdateException(String username, Throwable cause) {
        super("При обновлении ролей возникла проблема: " + username, cause);
    }
}
