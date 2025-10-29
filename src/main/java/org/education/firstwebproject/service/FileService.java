package org.education.firstwebproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exceptionHandler.FileEmptyException;
import org.education.firstwebproject.exceptionHandler.FileSizeExceededException;
import org.education.firstwebproject.exceptionHandler.FileUploadException;
import org.education.firstwebproject.model.FileMetadata;
import org.education.firstwebproject.model.MultipleUploadResult;
import org.education.firstwebproject.service.repository.FileRepository;
import org.education.firstwebproject.utils.Messages;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.education.firstwebproject.utils.AppConstants;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final StorageYandexService storageYandexService;
    private final FileRepository fileRepository;

    public void uploadSingleFile(MultipartFile file) {
        validateFile(file);
        storageYandexService.uploadFile(file);
    }

    public MultipleUploadResult uploadMultipleFiles(MultipartFile[] files) {
        int successCount = 0;
        List<String> failedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            try {
                validateFile(file);
                storageYandexService.uploadFile(file);
                successCount++;
            } catch (FileEmptyException | FileSizeExceededException e) {
                failedFiles.add(file.getOriginalFilename());
            } catch (FileUploadException e) {
                failedFiles.add(file.getOriginalFilename());
                log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            } catch (Exception e) {
                failedFiles.add(file.getOriginalFilename());
                log.error("Unexpected error uploading file: {}", file.getOriginalFilename(), e);
            }
        }

        return new MultipleUploadResult(successCount,failedFiles);
    }

    public void deleteFile(String fileName) {
        storageYandexService.deleteFile(fileName);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileEmptyException();
        }
        if (file.getSize() > AppConstants.MAX_FILE_SIZE_BYTES) {
            throw new FileSizeExceededException();
        }

        Optional<FileMetadata> optionalFileMetadata = fileRepository.findByName(file.getOriginalFilename());
        if (optionalFileMetadata.isPresent()) {
            throw new FileUploadException(Messages.FILE_ALREADY_BEEN_UPLOADED);
        }
    }

    public byte[] downloadFile(String fileName) {
        return storageYandexService.downloadFile(fileName);
    }
}