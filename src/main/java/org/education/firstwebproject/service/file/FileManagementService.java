package org.education.firstwebproject.service.file;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.audit.AuditEventPublisher;
import org.education.firstwebproject.audit.AuditableOperation;
import org.education.firstwebproject.exception.file.FileUploadException;
import org.education.firstwebproject.exception.file.StorageException;
import org.education.firstwebproject.exception.validation.DuplicateFileException;
import org.education.firstwebproject.exception.validation.FileEmptyException;
import org.education.firstwebproject.exception.messages.Messages;
import org.education.firstwebproject.model.dto.FileDownloadResponse;
import org.education.firstwebproject.model.dto.LoginResponse;
import org.education.firstwebproject.model.dto.MultipleUploadResponse;
import org.education.firstwebproject.model.enums.AuditOperation;
import org.education.firstwebproject.model.enums.ResponseStatus;
import org.education.firstwebproject.service.security.FileSecurityService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для управления файлами с проверкой прав доступа.
 * Обрабатывает загрузку, скачивание и удаление файлов.
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class FileManagementService {

    private final FileOperationFacadeService fileOperationFacade;
    private final FileSecurityService fileSecurityService;
    private final AuditEventPublisher auditEventPublisher;

    private static final String CONTENT_TYPE_DOWNLOAD = "application/octet-stream";

    /**
     * Загружает один файл с проверкой прав доступа.
     * Обновляет token пользователя после успешной загрузки.
     */
    @AuditableOperation(operation = AuditOperation.UPLOAD)
    public LoginResponse uploadSingleFile(MultipartFile file, HttpServletResponse response) {
        fileSecurityService.checkUploadPermission();
        fileOperationFacade.uploadFile(file);
        return fileSecurityService.updateTokenAfterUpload(response);
    }

    /**
     * Загружает несколько файлов с индивидуальной обработкой ошибок.
     * Каждый файл обрабатывается отдельно, ошибки не прерывают загрузку остальных.
     *
     * @param files массив файлов для загрузки
     * @return список результатов загрузки для каждого файла
     * @throws FileUploadException если не удалось загрузить ни один файл
     */
    public List<MultipleUploadResponse> uploadMultipleFiles(MultipartFile[] files) {
        List<MultipleUploadResponse> results = new ArrayList<>();
        int successCount = 0;

        for (MultipartFile file : files) {
            MultipleUploadResponse response = processSingleFile(file);
            results.add(response);
            if (response.getStatus() == ResponseStatus.SUCCESS) {
                successCount++;
            }
        }

        if (successCount == 0) {
            throw new FileUploadException(Messages.FILES_UPLOAD_ERROR);
        }

        return results;
    }

    /**
     * Удаляет файл по имени.
     */
    @AuditableOperation(operation = AuditOperation.DELETE)
    public void deleteFile(String fileName) {
        fileOperationFacade.deleteFile(fileName);
    }

    /**
     * Подготавливает файл для скачивания.
     * Загружает содержимое файла из хранилища и кодирует имя для HTTP заголовка.
     */
    @AuditableOperation(operation = AuditOperation.DOWNLOAD)
    public FileDownloadResponse prepareFileDownload(String fileName) {
        byte[] data = fileOperationFacade.downloadFile(fileName);

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return FileDownloadResponse.builder()
                .content(data)
                .fileName(encodedFileName)
                .contentType(CONTENT_TYPE_DOWNLOAD)
                .size(data.length)
                .build();
    }

    /**
     * Обрабатывает загрузку одного файла с перехватом всех типов ошибок.
     * Логирует успех и ошибки в audit.
     */
    private MultipleUploadResponse processSingleFile(MultipartFile file) {
        try {
            fileOperationFacade.uploadFile(file);

            auditEventPublisher.publish(this, AuditOperation.UPLOAD,
                    file.getOriginalFilename(), ResponseStatus.SUCCESS, null);

            return new MultipleUploadResponse(
                    ResponseStatus.SUCCESS,
                    file.getOriginalFilename(),
                    Messages.FILE_UPLOAD_SUCCESS
            );

        } catch (Exception e) {
            auditEventPublisher.publishFailure(this, AuditOperation.UPLOAD,
                    file.getOriginalFilename(), e);

            return createErrorResponse(file, e);
        }
    }

    /**
     * Создаёт ответ об ошибке с подходящим сообщением.
     */
    private MultipleUploadResponse createErrorResponse(MultipartFile file, Exception e) {
        String message;

        if (e instanceof ConstraintViolationException cve) {
            message = cve.getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .findFirst()
                    .orElse(Messages.FILE_VALIDATION_FAILED);
        } else if (e instanceof FileEmptyException || e instanceof DuplicateFileException) {
            message = e.getMessage();
        } else if (e instanceof FileUploadException || e instanceof StorageException) {
            message = e.getMessage();
        } else {
            message = Messages.UNEXPECTED_ERROR;
        }

        log.warn("File upload error for {}: {}", file.getOriginalFilename(), message);

        return new MultipleUploadResponse(
                ResponseStatus.ERROR,
                file.getOriginalFilename(),
                message
        );
    }
}