package com.othertales.modules.writing.infrastructure.persistence;

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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

/**
 * AUDIT FIX #11 (FASE 3.2): Unified targetWordCount validation to @Min(1).
 */
@Entity
@Table(name = "projects", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class ProjectEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank
    @Size(min = 1, max = 255)
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s.,!?'\":;()-]+$", message = "PROJECT_INVALID_TITLE")
    @Column(nullable = false, length = 255)
    private String title;

    @Size(max = 2000)
    @Column(length = 2000)
    private String synopsis;

    @Size(max = 100)
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s-]*$", message = "PROJECT_INVALID_GENRE")
    @Column(length = 100)
    private String genre;

    @Min(0)
    @Column(name = "current_word_count", nullable = false)
    private int currentWordCount;

    @Min(1)
    @Column(name = "target_word_count", nullable = false)
    private int targetWordCount;

    @Size(max = 500)
    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatusEntity status = ProjectStatusEntity.DRAFT;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "created_at", nullable = false)
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

    public enum ProjectStatusEntity {
        DRAFT, PUBLISHED
    }
}
