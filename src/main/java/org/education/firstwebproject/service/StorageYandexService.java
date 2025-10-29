package org.education.firstwebproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exceptionHandler.FileDeleteException;
import org.education.firstwebproject.exceptionHandler.FileDownloadException;
import org.education.firstwebproject.exceptionHandler.FileUploadException;
import org.education.firstwebproject.exceptionHandler.StorageException;
import org.education.firstwebproject.model.FileMetadata;
import org.education.firstwebproject.service.repository.FileRepository;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.education.firstwebproject.utils.Messages;
import org.education.firstwebproject.utils.YandexStorageProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageYandexService {

    private final FileRepository fileRepository;
    private final AmazonS3 YandexS3Client;
    private final YandexStorageProperties properties;

    public void uploadFile(MultipartFile file) {
        File fileObj = null;
        try {
            String originalFilename = file.getOriginalFilename();
            fileObj = convertMultiPartFileToFile(file);

            YandexS3Client.putObject(new PutObjectRequest(
                    properties.getBucketName(),
                    originalFilename,
                    fileObj));

            URL fileUrl = YandexS3Client.getUrl(properties.getBucketName(), originalFilename);

            saveFileInfoToDatabase(file, originalFilename, fileUrl.toString());

            log.info("File uploaded: {}", file.getOriginalFilename());
        } catch (AmazonS3Exception e) {
            log.error("S3 error uploading file: {}", file.getOriginalFilename(), e);
            throw new StorageException(Messages.FILE_STORAGE_ERROR + file.getOriginalFilename(), e);
        } catch (Exception e) {
            log.error("Error uploading file: {}", file.getOriginalFilename(), e);
            throw new FileUploadException(Messages.FILE_UPLOAD_ERROR + file.getOriginalFilename(), e);
        } finally {
            if (fileObj != null && fileObj.exists()) {
                fileObj.delete();
            }
        }
    }

    public byte[] downloadFile(String fileName) {
        try {
            S3Object s3Object = YandexS3Client.getObject(properties.getBucketName(), fileName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error("Error downloading file: {}", fileName, e);
            throw new FileDownloadException(Messages.FILE_DOWNLOAD_ERROR + fileName, e);
        }
    }

    @Transactional
    public void deleteFile(String fileName) {

        FileMetadata fileMetadata = fileRepository.findByName(fileName)
                .orElseThrow(() -> new NotFoundException("File not found in database: " + fileName));
        fileRepository.delete(fileMetadata);

        try {
            YandexS3Client.deleteObject(properties.getBucketName(), fileName);
            log.info("File deleted: {}", fileName);
        } catch (AmazonS3Exception e) {
            log.error("S3 error deleting file: {}", fileName, e);
            throw new StorageException(Messages.FILE_STORAGE_ERROR + fileName, e);
        } catch (Exception e) {
            log.error("Error deleting file: {}", fileName, e);
            throw new FileDeleteException(Messages.FILE_DELETE_ERROR + fileName, e);
        }
    }

    private void saveFileInfoToDatabase(MultipartFile file, String originalFileName, String fileUrl) {
       FileMetadata fileEntity = FileMetadata.builder()
                .name(originalFileName)
                .type(file.getContentType())
                .filePath(fileUrl)
                .size(file.getSize())
                .build();

        fileRepository.save(fileEntity);
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting file: {}", file.getOriginalFilename(), e);
            throw new FileUploadException(Messages.FILE_CONVERT_ERROR + file.getOriginalFilename(), e);
        }
        return convertedFile;
    }
}