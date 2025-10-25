package org.education.firstwebproject.exceptionHandler;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String username) {
        super("User already exists: " + username);
    }

    public UserAlreadyExistsException(String username, Throwable cause) {
        super("User already exists: " + username, cause);
    }
}
