package org.education.firstwebproject.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.model.FileUploadEvent;
import org.education.firstwebproject.model.entity.FileMetadata;
import org.education.firstwebproject.properties.YandexStorageProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileEventService {

    private final FileEventProducer fileEventProducer;
    private final YandexStorageProperties yandexStorageProperties;

    public void publishFileUploadEvent(FileMetadata fileMetadata, Long userId) {
        FileUploadEvent event = new FileUploadEvent(
                fileMetadata.getId(),
                fileMetadata.getUniqueName(),
                yandexStorageProperties.getBucketName(),
                fileMetadata.getOriginalName(),
                userId
        );

        fileEventProducer.sendFileUploadEvent(event);
        log.info("File upload event published for fileId={}", fileMetadata.getId());
    }
}
