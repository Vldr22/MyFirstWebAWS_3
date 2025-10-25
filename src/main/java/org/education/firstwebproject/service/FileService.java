package org.education.firstwebproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exceptionHandler.FileStorageException;
import org.education.firstwebproject.model.MultipleUploadResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final StorageYandexService storageYandexService;

    public String uploadSingleFile(MultipartFile file) {
        validateFile(file);
        storageYandexService.uploadFile(file);
        return "Файл '" + file.getOriginalFilename() + "' успешно загружен!";
    }

    public MultipleUploadResult uploadMultipleFiles(MultipartFile[] files) {
        log.info("Uploading {} files", files.length);

        int successCount = 0;
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            try {
                validateFile(file);
                storageYandexService.uploadFile(file);
                successCount++;
            } catch (IllegalArgumentException e) {
                errors.add("Файл '" + file.getOriginalFilename() + "': " + e.getMessage());
            } catch (FileStorageException e) {
                errors.add("Файл '" + file.getOriginalFilename() + "': ошибка загрузки");
                log.error("Failed to upload: {}", file.getOriginalFilename());
            } catch (Exception e) {
                errors.add("Файл '" + file.getOriginalFilename() + "': неожиданная ошибка");
                log.error("Unexpected error: {}", file.getOriginalFilename());
            }
        }

        return new MultipleUploadResult(successCount, errors);
    }

    public void deleteFile(String fileName) {
        storageYandexService.deleteFile(fileName);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }
        if (file.getSize() > AppConstants.MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "Размер файла превышает " + AppConstants.MAX_FILE_SIZE_MB + "MB");
        }
    }

    public byte[] downloadFile(String fileName) {
        return storageYandexService.downloadFile(fileName);
    }
}