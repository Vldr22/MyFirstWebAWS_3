package org.education.firstwebproject.service.file;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exception.file.StorageException;
import org.education.firstwebproject.exception.validation.DuplicateFileException;
import org.education.firstwebproject.exception.validation.FileEmptyException;
import org.education.firstwebproject.exception.file.FileUploadException;
import org.education.firstwebproject.model.response.FileDownloadResponse;
import org.education.firstwebproject.model.response.LoginResponse;
import org.education.firstwebproject.model.response.MultipleUploadResponse;
import org.education.firstwebproject.model.enums.ResponseStatus;
import org.education.firstwebproject.exception.messages.Messages;
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

    /**
     * Загружает один файл с проверкой прав доступа.
     * Обновляет токен пользователя после успешной загрузки.
     *
     * @param file файл для загрузки
     * @param response HTTP ответ для установки токена
     * @return обновлённые данные пользователя с новым токеном
     */
    public LoginResponse uploadSingleFile(MultipartFile file, HttpServletResponse response) {
        fileSecurityService.checkUploadPermission();
        fileOperationFacade.uploadFile(file);
        return fileSecurityService.updateTokenAfterUpload(response);
    }

    /**
     * Загружает несколько файлов с индивидуальной обработкой ошибок.
     * Каждый файл обрабатывается отдельно, ошибки не прерывают загрузку остальных файлов.
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
     *
     * @param fileName имя файла для удаления
     */
    public void deleteFile(String fileName) {
        fileOperationFacade.deleteFile(fileName);
    }

    /**
     * Подготавливает файл для скачивания.
     * Загружает содержимое файла из хранилища и кодирует имя для HTTP заголовка.
     *
     * @param fileName имя файла для скачивания
     * @return объект с содержимым файла и метаданными
     */
    public FileDownloadResponse prepareFileDownload(String fileName) {
        byte[] data = fileOperationFacade.downloadFile(fileName);

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return FileDownloadResponse.builder()
                .content(data)
                .fileName(encodedFileName)
                .contentType("application/octet-stream")
                .size(data.length)
                .build();
    }

    /**
     * Обрабатывает загрузку одного файла с перехватом всех типов ошибок.
     *
     * @param file файл для загрузки
     * @return результат загрузки (успех или ошибка)
     */
    private MultipleUploadResponse processSingleFile(MultipartFile file) {
        try {
            fileOperationFacade.uploadFile(file);
            return createSuccessResponse(file);

        } catch (ConstraintViolationException e) {
            return handleValidationError(file, e);

        } catch (FileEmptyException | DuplicateFileException e) {
            return handleValidationException(file, e);

        } catch (FileUploadException | StorageException e) {
            return handleUploadException(file, e);

        } catch (Exception e) {
            return handleUnexpectedError(file, e);
        }
    }

    /**
     * Создаёт ответ об успешной загрузке.
     */
    private MultipleUploadResponse createSuccessResponse(MultipartFile file) {
        return new MultipleUploadResponse(
                ResponseStatus.SUCCESS,
                file.getOriginalFilename(),
                Messages.FILE_UPLOAD_SUCCESS);
    }

    /**
     * Обрабатывает ошибку валидации файла (нарушение constraint-ов).
     */
    private MultipleUploadResponse handleValidationError(MultipartFile file, ConstraintViolationException e) {
        String message = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("File validation failed");

        log.warn("File validation failed: {}, Reason: {}", file.getOriginalFilename(), message);

        return new MultipleUploadResponse(
                ResponseStatus.ERROR,
                file.getOriginalFilename(),
                message);
    }

    /**
     * Обрабатывает ошибки валидации (пустой файл, дублирование).
     */
    private MultipleUploadResponse handleValidationException(MultipartFile file, Exception e) {
        log.warn("File validation error for {}: {}", file.getOriginalFilename(), e.getMessage());

        return new MultipleUploadResponse(
                ResponseStatus.ERROR,
                file.getOriginalFilename(),
                e.getMessage());
    }

    /**
     * Обрабатывает ошибки загрузки и хранилища.
     */
    private MultipleUploadResponse handleUploadException(MultipartFile file, Exception e) {
        log.error("Upload error for {}: {}", file.getOriginalFilename(), e.getMessage());

        return new MultipleUploadResponse(
                ResponseStatus.ERROR,
                file.getOriginalFilename(),
                e.getMessage());
    }

    /**
     * Обрабатывает неожиданные ошибки.
     */
    private MultipleUploadResponse handleUnexpectedError(MultipartFile file, Exception e) {
        log.error("Unexpected error uploading file: {}", file.getOriginalFilename(), e);

        return new MultipleUploadResponse(
                ResponseStatus.ERROR,
                file.getOriginalFilename(),
                Messages.UNEXPECTED_ERROR);
    }
}