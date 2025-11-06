package org.education.firstwebproject.service.file;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exception.file.StorageException;
import org.education.firstwebproject.exception.validation.DuplicateFileException;
import org.education.firstwebproject.exception.validation.FileEmptyException;
import org.education.firstwebproject.exception.file.FileUploadException;
import org.education.firstwebproject.model.response.LoginResponse;
import org.education.firstwebproject.model.response.MultipleUploadResponse;
import org.education.firstwebproject.model.enums.ResponseStatus;

import org.education.firstwebproject.exception.messages.Messages;
import org.education.firstwebproject.service.security.FileSecurityService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Сервис для управления файлами с проверкой прав доступа.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileManagementService {

    private final FileOperationFacadeService storageYandexService;
    private final FileSecurityService fileSecurityService;

    /**
     * Загружает файл с проверкой прав и обновлением токена пользователя.
     *
     * @return новый токен с обновленной ролью
     */
    public LoginResponse uploadSingleFile(MultipartFile file, HttpServletResponse response) {
        fileSecurityService.checkUploadPermission();
        storageYandexService.uploadFile(file);
        return fileSecurityService.updateTokenAfterUpload(response);
    }

    /**
     * Загружает несколько файлов с индивидуальной обработкой ошибок.
     * Возвращает результат для каждого файла (успех/ошибка).
     * Бросает исключение, только если ВСЕ файлы упали с ошибкой.
     *
     * @return список результатов загрузки для каждого файла
     */
    public List<MultipleUploadResponse> uploadMultipleFiles(MultipartFile[] files) {
        List<MultipleUploadResponse> multipleUploadResults = new ArrayList<>();
        int fileSuccessCount = 0;

        for (MultipartFile file : files) {
            try {
                storageYandexService.uploadFile(file);
                fileSuccessCount++;
                multipleUploadResults.add(new MultipleUploadResponse(
                        ResponseStatus.SUCCESS,
                        file.getOriginalFilename(),
                        Messages.FILE_UPLOAD_SUCCESS));

            } catch (FileEmptyException | DuplicateFileException e) {
                log.warn("Validation error for file {}: {}", file.getOriginalFilename(), e.getMessage());
                multipleUploadResults.add(new MultipleUploadResponse(
                        ResponseStatus.ERROR,
                        file.getOriginalFilename(),
                        e.getMessage()));

            } catch (FileUploadException | StorageException e) {
                log.error("Upload error for file {}: {}", file.getOriginalFilename(), e.getMessage());
                multipleUploadResults.add(new MultipleUploadResponse(
                        ResponseStatus.ERROR,
                        file.getOriginalFilename(),
                        Messages.FILE_UPLOAD_ERROR + e.getMessage()));

            } catch (Exception e) {
                log.error("Unexpected error uploading file: {}", file.getOriginalFilename(), e);
                multipleUploadResults.add(new MultipleUploadResponse(
                        ResponseStatus.ERROR,
                        file.getOriginalFilename(),
                        Messages.UNEXPECTED_ERROR + e.getMessage()));
            }
        }

        if (fileSuccessCount == 0) {
            throw new FileUploadException(Messages.FILES_UPLOAD_ERROR);
        }

        return multipleUploadResults;
    }


    public void deleteFile(String fileName) {
        storageYandexService.deleteFile(fileName);
    }

    public byte[] downloadFile(String filename) {
        return storageYandexService.downloadFile(filename);
    }

}