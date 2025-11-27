package org.education.firstwebproject.audit;

import lombok.Getter;
import org.education.firstwebproject.model.entity.User;
import org.education.firstwebproject.model.enums.AuditOperation;
import org.education.firstwebproject.model.enums.ResponseStatus;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Event для аудита операций.
 */
@Getter
public class AuditLogEvent extends ApplicationEvent {

    private final User user;

    private final String ipAddress;

    private final AuditOperation operation;

    private final String identifier;

    private final ResponseStatus status;

    private final String details;

    private final LocalDateTime createdAt;

    public AuditLogEvent(Object source, User user, String ipAddress, AuditOperation operation,
                         String identifier, ResponseStatus status, String details) {
        super(source);
        this.user = user;
        this.ipAddress = ipAddress;
        this.operation = operation;
        this.identifier = identifier;
        this.status = status;
        this.details = details;
        this.createdAt = LocalDateTime.now();
    }

    public String getSummary() {
        String userPart = user != null
                ? String.format("User '%s'", user.getUsername())
                : String.format("IP '%s'", ipAddress);
        return String.format("%s %s: %s", userPart, operation, status);
    }
}
