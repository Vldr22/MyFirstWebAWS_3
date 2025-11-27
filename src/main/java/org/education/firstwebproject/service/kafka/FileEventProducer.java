package org.education.firstwebproject.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.model.FileUploadEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileEventProducer {

    private static final String TOPIC = "file-upload-events";

    private final KafkaTemplate<String, FileUploadEvent> kafkaTemplate;

    public void sendFileUploadEvent(FileUploadEvent event) {
        log.info("Sending file upload event to Kafka: fileId={}, s3Key={}",
                event.getFileId(), event.getS3Key());

        kafkaTemplate.send(TOPIC, event.getFileId().toString(), event);

        log.info("File upload event sent successfully");
    }
}
