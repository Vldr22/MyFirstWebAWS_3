package org.education.firstwebproject.exception;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exception.file.*;
import org.education.firstwebproject.exception.validation.FileUploadLimitExceededException;
import org.education.firstwebproject.exception.user.UnauthorizedException;
import org.education.firstwebproject.exception.user.UserAlreadyExistsException;
import org.education.firstwebproject.exception.user.UserNotFoundException;
import org.education.firstwebproject.exception.validation.*;
import org.education.firstwebproject.model.CommonResponse;
import org.education.firstwebproject.exception.messages.Messages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Глобальный обработчик исключений для всех контроллеров.
 * Возвращает ошибки в формате {@link CommonResponse} с {@link ProblemDetail}.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    // ========== FILE OPERATIONS ==========

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public CommonResponse<Void> handleMaxSizeException() {
        log.error("File exceeds max upload size: {}", maxFileSize);
        String message = String.format("File size exceeds the maximum allowed limit of %s", maxFileSize);
        return createErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, "File Too Large", message);
    }

    @ExceptionHandler(FileEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleEmptyFile(FileEmptyException ex) {
        log.error("File is empty");
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Empty File", ex.getMessage());
    }

    @ExceptionHandler(DuplicateFileException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Void> handleDuplicateFile(DuplicateFileException ex) {
        log.error("Duplicate file: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.CONFLICT, "Duplicate File", ex.getMessage());
    }

    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void> handleFileUploadException(FileUploadException ex) {
        log.error("File upload error: {}", ex.getMessage());
        String message = ex.getMessage() != null ? ex.getMessage() : Messages.FILE_UPLOAD_ERROR;
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "File Upload Error", message);
    }

    @ExceptionHandler(FileDownloadException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void> handleFileDownloadException(FileDownloadException ex) {
        log.error("File download error: {}", ex.getMessage());
        String message = ex.getMessage() != null ? ex.getMessage() : Messages.FILE_DOWNLOAD_ERROR;
        return createErrorResponse(HttpStatus.NOT_FOUND, "File Not Found", message);
    }

    @ExceptionHandler(FileDeleteException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void> handleFileDeleteException(FileDeleteException ex) {
        log.error("File delete error: {}", ex.getMessage());
        String message = ex.getMessage() != null ? ex.getMessage() : Messages.FILE_DELETE_ERROR;
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "File Delete Error", message);
    }

    // ========== STORAGE ==========

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void> handleStorageException(StorageException ex) {
        log.error("Storage error: {}", ex.getMessage());
        String message = ex.getMessage() != null ? ex.getMessage() : Messages.FILE_STORAGE_ERROR;
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Storage Error", message);
    }

    @ExceptionHandler(AmazonS3Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleAmazonS3Exception(AmazonS3Exception ex) {
        log.error("S3 error: {} (status: {})", ex.getErrorCode(), ex.getStatusCode());

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getErrorMessage());
        problemDetail.setTitle("S3 Storage Error");

        return ResponseEntity
                .status(status)
                .body(CommonResponse.error(problemDetail));
    }

    // ========== VALIDATION ==========

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error");

        StringBuilder errors = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }

        return createErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", errors.toString());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation");

        String message = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Validation failed");

        return createErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", message);
    }

    // ========== HTTP REQUEST ==========
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleMissingParams(MissingServletRequestParameterException ex) {
        log.warn("Missing parameter: {}", ex.getParameterName());
        String message = String.format("Required parameter '%s' is missing", ex.getParameterName());
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Missing Parameter", message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch: {}", ex.getName());

        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Invalid type for parameter '%s'. Expected: %s", ex.getName(), requiredType);
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Type Mismatch", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleHttpMessageNotReadable() {
        log.warn("Malformed request");
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Malformed Request", "Invalid request format (check JSON syntax)");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void> handleNoResourceFound(NoResourceFoundException ex) {
        log.warn("Resource not found: {}", ex.getResourcePath());
        return createErrorResponse(HttpStatus.NOT_FOUND, "Resource Not Found", "Requested resource not found");
    }

    // ========== DATABASE ==========

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Void> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());

        String message = "Data integrity violation";
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("unique")) {
                message = "Record with this data already exists";
            } else if (ex.getMessage().contains("foreign key")) {
                message = "Cannot delete - related records exist";
            }
        }

        return createErrorResponse(HttpStatus.CONFLICT, "Data Integrity Error", message);
    }

    @ExceptionHandler({EntityNotFoundException.class, EmptyResultDataAccessException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void> handleEntityNotFound(Exception ex) {
        log.error("Entity not found: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.NOT_FOUND, "Not Found", "Requested entity not found");
    }

    // ========== SECURITY ==========

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResponse<Void> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.FORBIDDEN, "Access Denied", "Insufficient permissions to perform this operation");
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse<Void> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Unauthorized: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid password or login");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Void> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("User already exists: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.CONFLICT, "User Already Exists", ex.getMessage());
    }

    @ExceptionHandler(FileUploadLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Void> handleFileUploadLimitExceeded(FileUploadLimitExceededException ex) {
        log.warn("User already added the file: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.CONFLICT, "Already Added", ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse<Void> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication required");
    }

    // ========== GENERAL ==========

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Argument", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void> handleGeneralException(Exception ex) {
        log.error("Unexpected exception", ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An error occurred. Please try again later");
    }

    // ========== HELPER METHOD ==========

    private CommonResponse<Void> createErrorResponse(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return CommonResponse.error(problemDetail);
    }
}