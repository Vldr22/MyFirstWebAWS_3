package org.education.firstwebproject.service.file;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.education.firstwebproject.exception.messages.Messages;
import org.education.firstwebproject.model.entity.FileMetadata;
import org.education.firstwebproject.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Сервис для управления метаданными файлов в БД.
 */
@Service
@RequiredArgsConstructor
public class FileMetadataService {

    private final FileMetadataRepository fileMetadataRepository;

    public void saveDatabaseMetadata(MultipartFile file, String uniqueName,  String fileHash) {
        FileMetadata metadata = FileMetadata.builder()
                .uniqueName(uniqueName)
                .originalName(file.getOriginalFilename())
                .type(file.getContentType())
                .size(file.getSize())
                .fileHash(fileHash)
                .build();

        fileMetadataRepository.save(metadata);
    }

    public void deleteDatabaseMetadata(String uniqueName) {
        FileMetadata metadata = fileMetadataRepository.findByUniqueName(uniqueName)
                .orElseThrow(() -> new EntityNotFoundException(String.format(Messages.FILE_NOT_FOUND, uniqueName)));
        fileMetadataRepository.delete(metadata);
    }

    public String getUniqueNameByOriginalFilename(String originalFileName) {
        FileMetadata fileMetadata = fileMetadataRepository.findByOriginalName(originalFileName)
                .orElseThrow(() -> new EntityNotFoundException(String.format(Messages.FILE_NOT_FOUND, originalFileName)));
        return fileMetadata.getUniqueName();
    }
}
