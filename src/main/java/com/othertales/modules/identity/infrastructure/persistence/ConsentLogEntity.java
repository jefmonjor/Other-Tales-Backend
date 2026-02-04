package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.domain.ConsentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Immutable audit log for consent changes (GDPR compliance).
 *
 * <p>This entity tracks ALL consent changes with full traceability:</p>
 * <ul>
 *   <li>Who changed consent (user_id)</li>
 *   <li>What type of consent changed</li>
 *   <li>What was the new value (granted/revoked)</li>
 *   <li>When it happened</li>
 *   <li>From which IP address</li>
 *   <li>Which user agent was used</li>
 * </ul>
 *
 * <p>Records are IMMUTABLE - never update, only insert new records.</p>
 */
@Entity
@Table(name = "consent_logs", schema = "public", indexes = {
        @Index(name = "idx_consent_logs_user_id", columnList = "user_id"),
        @Index(name = "idx_consent_logs_consent_type", columnList = "consent_type"),
        @Index(name = "idx_consent_logs_recorded_at", columnList = "recorded_at")
})
@Getter
@Setter
@NoArgsConstructor
public class ConsentLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false, updatable = false, length = 50)
    private ConsentType consentType;

    @Column(name = "consent_given", nullable = false, updatable = false)
    private boolean consentGiven;

    @Column(name = "ip_address", length = 45, updatable = false)
    private String ipAddress;

    @Column(name = "user_agent", length = 500, updatable = false)
    private String userAgent;

    @Column(name = "recorded_at", nullable = false, updatable = false)
    private OffsetDateTime recordedAt;

    @PrePersist
    void prePersist() {
        if (recordedAt == null) {
            recordedAt = OffsetDateTime.now();
        }
    }

    /**
     * Factory method for creating a consent log entry.
     */
    public static ConsentLogEntity create(
            UUID userId,
            ConsentType consentType,
            boolean consentGiven,
            String ipAddress,
            String userAgent
    ) {
        var log = new ConsentLogEntity();
        log.setUserId(userId);
        log.setConsentType(consentType);
        log.setConsentGiven(consentGiven);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setRecordedAt(OffsetDateTime.now());
        return log;
    }
}
