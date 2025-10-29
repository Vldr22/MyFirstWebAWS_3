package org.education.firstwebproject.exceptionHandler;

public class FileEmptyException extends FileValidationException {

    public FileEmptyException() {
        super("File is empty");
    }
}
