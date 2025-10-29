package org.education.firstwebproject.exceptionHandler;

import org.education.firstwebproject.utils.Messages;

public class RoleUpdateException extends RuntimeException {
    public RoleUpdateException(String username) {
        super(String.format(Messages.USER_ROLE_NOT_UPDATED, username));
    }
}
