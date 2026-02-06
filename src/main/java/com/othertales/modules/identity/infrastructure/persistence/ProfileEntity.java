package com.othertales.modules.identity.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

/**
 * AUDIT FIX #12 (FASE 3.3): Standardized ALL timestamps to Instant (was OffsetDateTime).
 */
@Entity
@Table(name = "profiles", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class ProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String email;

    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[\\p{L}\\s'-]+$", message = "PROFILE_INVALID_NAME")
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanTypeEntity planType = PlanTypeEntity.FREE;

    @Column(name = "terms_accepted", nullable = false)
    private boolean termsAccepted = false;

    @Column(name = "terms_accepted_at")
    private Instant termsAcceptedAt;

    @Column(name = "privacy_accepted", nullable = false)
    private boolean privacyAccepted = false;

    @Column(name = "privacy_accepted_at")
    private Instant privacyAcceptedAt;

    @Column(name = "marketing_accepted", nullable = false)
    private boolean marketingAccepted = false;

    @Column(name = "marketing_accepted_at")
    private Instant marketingAcceptedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    public enum PlanTypeEntity {
        FREE, PRO
    }
}
