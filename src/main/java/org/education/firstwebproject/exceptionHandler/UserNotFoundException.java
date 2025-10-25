package org.education.firstwebproject.exceptionHandler;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
    }

    public UserNotFoundException(String username) {
        super("User not found with username: " + username);
    }

}
