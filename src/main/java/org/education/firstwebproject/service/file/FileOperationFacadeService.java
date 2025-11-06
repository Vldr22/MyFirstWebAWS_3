package org.education.firstwebproject.service.file;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exception.file.*;
import org.education.firstwebproject.service.storage.YandexStorageService;
import org.education.firstwebproject.validation.ValidFile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.education.firstwebproject.exception.messages.Messages;

import java.io.IOException;
import java.util.UUID;

/**
 * Фасад для операций с файлами (загрузка, скачивание, удаление).
 * Координирует работу с S3 хранилищем, БД и валидацией.
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class FileOperationFacadeService {

    private final FileHashService fileHashService;
    private final YandexStorageService fileStorageService;
    private final FileMetadataService fileMetadataService;

    /**
     * Загружает файл в S3 и сохраняет метаданные в БД.
     * При ошибке сохранения в БД откатывает загрузку в S3.
     */
    public void uploadFile(@ValidFile MultipartFile file) {
        String uniqueFileName = generateUniqueUUIDFileName(file.getOriginalFilename());

        try {
            byte[] bytes = file.getBytes();
            String fileHash = fileHashService.calculateMD5(bytes);
            fileHashService.checkDuplicateInDatabase(fileHash);

            fileStorageService.uploadFileYandexS3(uniqueFileName, bytes, file.getContentType());

            try {
                fileMetadataService.saveDatabaseMetadata(file, uniqueFileName, fileHash);
                log.info("File uploaded successfully: {}", uniqueFileName);
            } catch (Exception dbEx) {
                log.error("Error add fileMetadata in database file with original fileName{}",
                        file.getOriginalFilename(), dbEx);
                fileStorageService.rollbackS3Upload(uniqueFileName);
                throw new FileUploadException(Messages.FILE_UPLOAD_ERROR + file.getOriginalFilename(), dbEx);
            }

        } catch (IOException e) {
            log.error("I/O error processing file: {}", file.getOriginalFilename(), e);
            throw new FileUploadException(Messages.FILE_UPLOAD_ERROR + file.getOriginalFilename(), e);
        }
    }

    /**
     * Загружает файл из S3 если файл есть в БД
     */
    public byte[] downloadFile(String fileName) {
        String uniqueName = fileMetadataService.getUniqueNameByOriginalFilename(fileName);
        return fileStorageService.downloadFileYandexS3(uniqueName);
    }

    /**
     * Удаляет файл из Yandex Object Storage и его метаданные из БД.
     * Порядок удаления: сначала S3, затем БД (для предотвращения потерянных файлов).
     */
    @Transactional
    public void deleteFile(String fileName) {
        String uniqueName = fileMetadataService.getUniqueNameByOriginalFilename(fileName);
        fileStorageService.deleteFileYandexS3(uniqueName);
        fileMetadataService.deleteDatabaseMetadata(uniqueName);
        log.info("File {} deleted successfully from S3 and DB", fileName);
    }

    /**
     * Генерирует уникальное имя файла: UUID.extension
     */
    private String generateUniqueUUIDFileName(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        extension = (extension != null && !extension.isBlank()) ? extension : "tmp";

        return UUID.randomUUID() + "." + extension;
    }
}