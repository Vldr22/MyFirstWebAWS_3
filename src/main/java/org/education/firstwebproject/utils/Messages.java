package org.education.firstwebproject.utils;

public class Messages {

    private Messages() {}

    // User messages
    public static final String USER_NOT_FOUND = "User not found with username: %s";
    public static final String USER_ALREADY_EXISTS = "User already exists: %s";
    public static final String USER_ROLE_NOT_UPDATED = "User role has not been updated: %s";
    public static final String ROLE_NOT_FOUND = "Role not found: %s";
    public static final String REGISTRATION_SUCCESS = "Registration was successful! You can now log in!";

    // Single file upload
    public static final String FILE_NOT_SELECTED = "Please select a file";
    public static final String FILE_UPLOAD_SUCCESS = "File '%s' uploaded successfully";
    public static final String FILE_UPLOAD_ERROR = "Failed to upload file";
    public static final String FILE_EMPTY = "File is empty";
    public static final String FILE_ALREADY_BEEN_UPLOADED = "File already been uploaded";
    public static final String FILE_SIZE_EXCEEDED = "File size exceeds " + AppConstants.MAX_FILE_SIZE_MB +"MB";
    public static final String FILE_CONVERT_ERROR = "Failed to convert file";
    public static final String INABILITY_UPLOAD_MORE_THAN_ONE_FILE = "You have already uploaded a file. According to our service rules," +
            " one authorized user can only upload 1 file.";

    // Multiple files upload
    public static final String FILES_NOT_SELECTED = "Please select files";
    public static final String FILES_UPLOAD_SUCCESS = "Successfully uploaded %d files";
    public static final String FILES_UPLOAD_PARTIAL = "Uploaded: %d, Failed: %d";
    public static final String FILES_UPLOAD_FAILED = "Failed to upload files. Failed files: %s";

    // File deletion
    public static final String FILE_DELETE_SUCCESS = "File deleted successfully: %s";
    public static final String FILE_DELETE_ERROR = "Failed to delete file: %s";
    public static final String FILE_DELETE_UNEXPECTED_ERROR = "An error occurred while deleting the file";

    // Storage operations
    public static final String FILE_STORAGE_ERROR = "Unable to process a file operation in the storage";
    public static final String FILE_DOWNLOAD_ERROR = "Failed to download file";

    // General
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred. Please try again later.";
    public static final String REGISTER_UNEXPECTED_ERROR = "An error occurred during registration. Please try again later";
    public static final String INVALID_REQUEST = "Invalid request: ";
}
