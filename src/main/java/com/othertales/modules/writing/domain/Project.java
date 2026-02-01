package com.othertales.modules.writing.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Project {

    private UUID id;
    private UUID userId;
    private String title;
    private String synopsis;
    private String coverUrl;
    private ProjectStatus status;
    private boolean deleted;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    private Project() {}

    public static Project create(UUID userId, String title, String synopsis) {
        validateTitle(title);

        var project = new Project();
        project.id = UUID.randomUUID();
        project.userId = Objects.requireNonNull(userId, "User ID is required");
        project.title = title.trim();
        project.synopsis = synopsis;
        project.coverUrl = null;
        project.status = ProjectStatus.DRAFT;
        project.deleted = false;
        project.createdAt = Instant.now();
        project.updatedAt = project.createdAt;
        project.version = 0L;
        return project;
    }

    public static Project reconstitute(
            UUID id,
            UUID userId,
            String title,
            String synopsis,
            String coverUrl,
            ProjectStatus status,
            boolean deleted,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        var project = new Project();
        project.id = id;
        project.userId = userId;
        project.title = title;
        project.synopsis = synopsis;
        project.coverUrl = coverUrl;
        project.status = status;
        project.deleted = deleted;
        project.createdAt = createdAt;
        project.updatedAt = updatedAt;
        project.version = version;
        return project;
    }

    public void updateTitle(String newTitle) {
        validateTitle(newTitle);
        this.title = newTitle.trim();
        this.updatedAt = Instant.now();
    }

    public void updateSynopsis(String newSynopsis) {
        this.synopsis = newSynopsis;
        this.updatedAt = Instant.now();
    }

    public void publish() {
        this.status = ProjectStatus.PUBLISHED;
        this.updatedAt = Instant.now();
    }

    public void markAsDeleted() {
        this.deleted = true;
        this.updatedAt = Instant.now();
    }

    private static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidProjectTitleException();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
