package org.education.firstwebproject.exception.messages;

/**
 * Сообщения в основном используемые в исключениях
 */
public class Messages {

    private Messages() {
    }

    /**
     * Сообщения связанные с пользователем
     */
    public static final String USER_NOT_FOUND = "User not found with username: %s";
    public static final String USER_ALREADY_EXISTS = "User already exists: ";
    public static final String ROLE_NOT_FOUND = "Role not found: %s";
    public static final String REGISTRATION_SUCCESS = "Registration was successful! You can now log in!";
    public static final String INVALID_PASSWORD_OR_LOGIN = "Invalid password or login";
    public static final String LOGOUT_SUCCESS = "Logged out successfully";
    public static final String REQUEST_LIMIT_EXCEEDED = "Request limit exceeded. Try later.";

    /**
     * Сообщения связанные с загрузкой файлов
     */
    public static final String FILE_NOT_FOUND = "File not found: %s";
    public static final String FILE_UPLOAD_SUCCESS = "File uploaded successfully";
    public static final String FILE_UPLOAD_ERROR = "Failed to upload file";
    public static final String FILE_EMPTY = "File is empty";
    public static final String FILE_ALREADY_BEEN_UPLOADED = "File already been uploaded";
    public static final String INABILITY_UPLOAD_MORE_THAN_ONE_FILE =
            "You have already uploaded a file. According to our service rules, one authorized user can only upload 1 file.";
    public static final String FILES_UPLOAD_ERROR = "Failed to upload all added files";


    /**
     * Сообщения связанные с удалением файлов
     */
    public static final String FILE_DELETE_SUCCESS = "File deleted successfully: %s";
    public static final String FILE_DELETE_ERROR = "Failed to delete file: %s";

    /**
     * Сообщения связанные с операциями S3 хранилища
     */
    public static final String FILE_STORAGE_ERROR = "Unable to process the file operation in the storage";
    public static final String FILE_DOWNLOAD_ERROR = "Failed to download file with name '%s'";

    public static final String UNEXPECTED_ERROR = "An unexpected error occurred. Please try again later.";
}
