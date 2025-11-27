package org.education.firstwebproject.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.model.entity.User;
import org.education.firstwebproject.model.enums.AuditOperation;
import org.education.firstwebproject.model.enums.ResponseStatus;
import org.education.firstwebproject.utils.MySecurityUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Публикует audit события.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Публикует audit событие.
     */
    public void publish(Object source, AuditOperation operation,
                        String identifier, ResponseStatus status, String details) {
        try {
            User user = MySecurityUtils.getCurrentUserOptional().orElse(null);
            String ipAddress = MySecurityUtils.getClientIpOrUnknown();

            AuditLogEvent event = new AuditLogEvent(
                    source, user, ipAddress, operation,
                    identifier, status, details
            );

            eventPublisher.publishEvent(event);
            log.debug("Audit event published: {} - {}", operation, identifier);

        } catch (Exception e) {
            log.error("Failed to publish audit event", e);
        }
    }

    public void publishFailure(Object source, AuditOperation operation,
                               String identifier, Exception exception) {
        publish(source, operation, identifier, ResponseStatus.FAILED,
                exception.getMessage());
    }
}
