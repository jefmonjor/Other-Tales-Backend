package com.othertales.modules.writing.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Idea {

    private UUID id;
    private UUID projectId;
    private String title;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean deleted;

    private Idea() {
    }

    public static Idea create(UUID projectId, String title, String content) {
        var idea = new Idea();
        idea.id = UUID.randomUUID();
        idea.projectId = Objects.requireNonNull(projectId, "Project ID is required");
        idea.title = Objects.requireNonNull(title, "Title is required").trim();
        if (idea.title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        idea.content = content != null ? content.trim() : null;
        idea.createdAt = Instant.now();
        idea.updatedAt = idea.createdAt;
        idea.deleted = false;
        return idea;
    }

    public static Idea reconstitute(
            UUID id,
            UUID projectId,
            String title,
            String content,
            Instant createdAt,
            Instant updatedAt,
            boolean deleted) {
        var idea = new Idea();
        idea.id = id;
        idea.projectId = projectId;
        idea.title = title;
        idea.content = content;
        idea.createdAt = createdAt;
        idea.updatedAt = updatedAt;
        idea.deleted = deleted;
        return idea;
    }

    public void update(String title, String content) {
        if (title != null) {
            if (title.isBlank()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }
            this.title = title.trim();
        }
        if (content != null) {
            this.content = content.trim();
        }

        this.updatedAt = Instant.now();
    }

    public void markAsDeleted() {
        this.deleted = true;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Idea idea = (Idea) o;
        return Objects.equals(id, idea.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
