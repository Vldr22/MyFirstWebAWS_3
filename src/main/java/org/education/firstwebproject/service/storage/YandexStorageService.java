package org.education.firstwebproject.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exception.file.FileDeleteException;
import org.education.firstwebproject.exception.file.FileDownloadException;
import org.education.firstwebproject.exception.file.StorageException;
import org.education.firstwebproject.exception.messages.Messages;
import org.education.firstwebproject.properties.YandexStorageProperties;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Сервис для работы с Yandex S3 хранилищем.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YandexStorageService {

    private final AmazonS3 yandexS3Client;
    private final YandexStorageProperties properties;

    public String uploadFileYandexS3(String uniqueFileName, byte[] bytes, String contentType) {
        ObjectMetadata metadata = createObjectMetadata(bytes, contentType);

        try (InputStream is = new ByteArrayInputStream(bytes)) {
            PutObjectRequest request = new PutObjectRequest(properties.getBucketName(), uniqueFileName, is, metadata);
            yandexS3Client.putObject(request);
        } catch (IOException e) {
            throw new StorageException(String.format(Messages.FILE_STORAGE_ERROR, uniqueFileName), e);
        }

        return yandexS3Client.getUrl(properties.getBucketName(), uniqueFileName).toString();
    }

    public byte[] downloadFileYandexS3(String fileName) {
        try (S3Object s3Object = yandexS3Client.getObject(properties.getBucketName(), fileName);
             S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            return IOUtils.toByteArray(inputStream);
        } catch (AmazonS3Exception e) {
            log.error("File not found in S3: {}", fileName);
            throw new StorageException(String.format(Messages.FILE_STORAGE_ERROR, fileName), e);
        } catch (IOException e) {
            log.error("Error downloading file: {}", fileName, e);
            throw new FileDownloadException(String.format(Messages.FILE_DOWNLOAD_ERROR, fileName), e);
        }
    }

    public void deleteFileYandexS3(String fileName) {
        try {
            yandexS3Client.deleteObject(properties.getBucketName(), fileName);
            log.info("File deleted from S3: {}", fileName);
        } catch (AmazonS3Exception e) {
            log.error("S3 error deleting file: {}", fileName, e);
            throw new StorageException(String.format(Messages.FILE_STORAGE_ERROR, fileName), e);
        } catch (Exception e) {
            log.error("Unexpected error deleting file: {}", fileName, e);
            throw new FileDeleteException(String.format(Messages.FILE_DELETE_ERROR, fileName), e);
        }
    }

    public void rollbackS3Upload(String uniqueFileName) {
        try {
            yandexS3Client.deleteObject(properties.getBucketName(), uniqueFileName);
            log.info("Successfully rolled back S3 upload for: {}", uniqueFileName);
        } catch (Exception deleteException) {
            log.error("Failed to rollback S3 upload: {}", uniqueFileName, deleteException);
        }
    }

    private ObjectMetadata createObjectMetadata(byte[] bytes, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType(contentType);
        return metadata;
    }
}
