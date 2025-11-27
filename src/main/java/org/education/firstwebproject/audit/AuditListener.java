package org.education.firstwebproject.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.model.entity.AuditLog;
import org.education.firstwebproject.repository.AuditLogRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;


/**
 * Слушатель для audit событий.
 * Асинхронно сохраняет логи в БД.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditListener {

    private final AuditLogRepository auditLogRepository;

    @Async
    @EventListener
    public void onAuditLogEvent(AuditLogEvent event) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .user(event.getUser())
                    .ipAddress(event.getIpAddress())
                    .operation(event.getOperation().name())
                    .fileName(event.getIdentifier())
                    .createdAt(Instant.now().toEpochMilli())
                    .status(event.getStatus())
                    .details(event.getDetails())
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Audit logged: {}", event.getSummary());

        } catch (Exception e) {
            log.error("Failed to log audit event", e);
        }
    }
}
