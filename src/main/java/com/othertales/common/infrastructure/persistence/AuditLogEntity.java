package com.othertales.common.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Generic audit log entity for tracking all system events.
 * Uses PostgreSQL JSONB for flexible detail storage.
 *
 * <p>Action types follow the pattern: ENTITY.ACTION (e.g., PROJECT.CREATED, CONSENT.UPDATED)</p>
 */
@Entity
@Table(name = "app_audit_logs", schema = "public", indexes = {
        @Index(name = "idx_audit_logs_user_id", columnList = "user_id"),
        @Index(name = "idx_audit_logs_action_type", columnList = "action_type"),
        @Index(name = "idx_audit_logs_entity_id", columnList = "entity_id"),
        @Index(name = "idx_audit_logs_performed_at", columnList = "performed_at")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", updatable = false)
    private UUID userId;

    @Column(name = "action_type", nullable = false, updatable = false, length = 100)
    private String actionType;

    @Column(name = "entity_id", updatable = false, length = 100)
    private String entityId;

    @Column(name = "details", columnDefinition = "jsonb", updatable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> details = new HashMap<>();

    @Column(name = "ip_address", length = 45, updatable = false)
    private String ipAddress;

    @Column(name = "user_agent", length = 500, updatable = false)
    private String userAgent;

    @Column(name = "performed_at", nullable = false, updatable = false)
    private Instant performedAt;

    @PrePersist
    void prePersist() {
        if (performedAt == null) {
            performedAt = Instant.now();
        }
    }

    /**
     * Factory method for creating an audit log entry.
     */
    public static AuditLogEntity create(
            UUID userId,
            String actionType,
            String entityId,
            Map<String, Object> details,
            String ipAddress,
            String userAgent
    ) {
        var log = new AuditLogEntity();
        log.setUserId(userId);
        log.setActionType(actionType);
        log.setEntityId(entityId);
        log.setDetails(details != null ? details : new HashMap<>());
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setPerformedAt(Instant.now());
        return log;
    }

    // Common action type constants
    public static final class Actions {
        public static final String CONSENT_UPDATED = "CONSENT.UPDATED";
        public static final String PROJECT_CREATED = "PROJECT.CREATED";
        public static final String PROJECT_UPDATED = "PROJECT.UPDATED";
        public static final String PROJECT_DELETED = "PROJECT.DELETED";
        public static final String PROFILE_UPDATED = "PROFILE.UPDATED";
        public static final String USER_LOGIN = "USER.LOGIN";
        public static final String USER_LOGOUT = "USER.LOGOUT";

        private Actions() {}
    }
}
