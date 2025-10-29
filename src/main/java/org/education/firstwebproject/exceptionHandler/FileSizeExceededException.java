package org.education.firstwebproject.exceptionHandler;

import org.education.firstwebproject.utils.AppConstants;

public class FileSizeExceededException extends FileValidationException {

    public FileSizeExceededException() {
        super("File size exceeds maximum allowed size of " + AppConstants.MAX_FILE_SIZE_MB + " MB");
    }
}
