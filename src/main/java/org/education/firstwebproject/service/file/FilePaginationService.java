package org.education.firstwebproject.service.file;

import lombok.RequiredArgsConstructor;
import org.education.firstwebproject.model.entity.FileMetadata;
import org.education.firstwebproject.model.response.FileResponse;
import org.education.firstwebproject.repository.FileMetadataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Сервис для пагинации файлов с преобразованием в DTO.
 */
@Service
@RequiredArgsConstructor
public class FilePaginationService {

    private final FileMetadataRepository fileMetadataRepository;
    private static final String MB_SUFFIX = " MB";

    public Page<FileResponse> paginationFiles(Pageable pageable) {
        Page<FileMetadata> filesPage = fileMetadataRepository.findAll(pageable);
        return filesPage.map(file -> FileResponse.builder()
                .fileName(file.getOriginalName())
                .fileSize(convertToMB(file.getSize()) + MB_SUFFIX)
                .build());
    }

    private String convertToMB(long bytes) {
        double mb = bytes / 1_048_576.0;
        return String.format("%.2f", mb);
    }
}
