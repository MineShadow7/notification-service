package org.hwmoodle.core.model;

import jakarta.persistence.*;
import org.hwmoodle.core.dto.UserEventOperation;

import java.time.Instant;

@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private UserEventOperation operation;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 1000)
    private String body;

    @Column(nullable = false)
    private Instant createdAt;

    protected NotificationEntity() {
    }

    public NotificationEntity(String email, UserEventOperation operation, String subject, String body) {
        this.email = email;
        this.operation = operation;
        this.subject = subject;
        this.body = body;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserEventOperation getOperation() {
        return operation;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

