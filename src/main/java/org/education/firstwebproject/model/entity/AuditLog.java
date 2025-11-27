package org.education.firstwebproject.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.education.firstwebproject.model.enums.ResponseStatus;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_timestamp", columnList = "created_at"),
        @Index(name = "idx_operation", columnList = "operation")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 45)
    private String ipAddress;

    @Column(nullable = false, length = 50)
    private String operation;

    @Column(length = 1024)
    private String fileName;

    @Column(nullable = false)
    private long createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResponseStatus status;

    @Column(columnDefinition = "TEXT")
    private String details;
}

