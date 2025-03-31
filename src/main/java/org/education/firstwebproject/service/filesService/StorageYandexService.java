package org.education.firstwebproject.service.filesService;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import jakarta.transaction.Transactional;
import org.education.firstwebproject.exceptionHandler.StorageException;
import org.education.firstwebproject.service.filesService.repository.FileRepository;
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
public class StorageYandexService {

    private final FileRepository fileRepository;

    @Value("${bucketName}")
    private String bucketName;

    private final AmazonS3 YandexS3Client;

    public StorageYandexService(FileRepository fileRepository, AmazonS3 yandexS3Client) {
        this.fileRepository = fileRepository;
        this.YandexS3Client = yandexS3Client;
    }

    public void saveFileInfoToDatabase(MultipartFile file, String filePath) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Не удалось сохранить файл: " +
                        file.getOriginalFilename() + " файл пуст");
            }
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }

        try {
            fileRepository.save(org.education.firstwebproject.dto.File.builder()
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .filePath(filePath)
                    .size(file.getSize())
                    .build());
        } catch (StorageException e) {
            throw new StorageException(e.getMessage());
        }
    }

    public boolean uploadFile(MultipartFile file) {
        boolean result = false;
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = file.getOriginalFilename();
        URL fileUrl = YandexS3Client.getUrl(bucketName, fileName);

        try {
            saveFileInfoToDatabase(file, String.valueOf(fileUrl));
            YandexS3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            fileObj.delete();
            result = true;
        } catch (AmazonS3Exception e) {
            throw new StorageException(e.getMessage());
        }
        return result;
    }

    public byte[] downloadFile(String fileName) {
        S3Object s3Object = YandexS3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
        return convertedFile;
    }
}
