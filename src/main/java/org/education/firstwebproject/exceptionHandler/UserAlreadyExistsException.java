package org.education.firstwebproject.exceptionHandler;

import org.education.firstwebproject.utils.Messages;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String username) {
        super(String.format(Messages.USER_ALREADY_EXISTS, username));
    }
}
