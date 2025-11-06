package org.education.firstwebproject.exception.validation;

public class FileUploadLimitExceededException extends RuntimeException {
    public FileUploadLimitExceededException(String message) {
        super(message);
    }
}
