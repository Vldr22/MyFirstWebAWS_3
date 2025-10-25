package org.education.firstwebproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exceptionHandler.FileDownloadException;
import org.education.firstwebproject.exceptionHandler.FileStorageException;
import org.education.firstwebproject.service.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StorageYandexService {

    private final FileRepository fileRepository;
    private final AmazonS3 YandexS3Client;

    @Value("${bucketName}")
    private String bucketName;

    public void uploadFile(MultipartFile file) {
        File fileObj = null;
        try {
            fileObj = convertMultiPartFileToFile(file);
            String fileName = file.getOriginalFilename();
            URL fileUrl = YandexS3Client.getUrl(bucketName, fileName);

            YandexS3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            saveFileInfoToDatabase(file, String.valueOf(fileUrl));

            log.info("File uploaded: {}", fileName);
        } catch (AmazonS3Exception e) {
            log.error("S3 error uploading file: {}", file.getOriginalFilename(), e);
            throw new FileStorageException("Failed to upload file to storage: " + file.getOriginalFilename(), e);
        } catch (Exception e) {
            log.error("Error uploading file: {}", file.getOriginalFilename(), e);
            throw new FileStorageException("Failed to upload file: " + file.getOriginalFilename(), e);
        } finally {
            if (fileObj != null && fileObj.exists()) {
                fileObj.delete();
            }
        }
    }

    public byte[] downloadFile(String fileName) {
        try {
            S3Object s3Object = YandexS3Client.getObject(bucketName, fileName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error("Error downloading file: {}", fileName, e);
            throw new FileDownloadException("Failed to download file: " + fileName, e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            YandexS3Client.deleteObject(bucketName, fileName);

            fileRepository.findByName(fileName)
                    .ifPresent(fileRepository::delete);

            log.info("File deleted: {}", fileName);
        } catch (AmazonS3Exception e) {
            log.error("S3 error deleting file: {}", fileName, e);
            throw new FileStorageException("Failed to delete file from storage: " + fileName, e);
        } catch (Exception e) {
            log.error("Error deleting file: {}", fileName, e);
            throw new FileStorageException("Failed to delete file: " + fileName, e);
        }
    }

    private void saveFileInfoToDatabase(MultipartFile file, String filePath) {
        org.education.firstwebproject.model.File fileEntity = org.education.firstwebproject.model.File.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(filePath)
                .size(file.getSize())
                .build();

        fileRepository.save(fileEntity);
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new FileStorageException("Failed to convert file: " + file.getOriginalFilename(), e);
        }
        return convertedFile;
    }
}