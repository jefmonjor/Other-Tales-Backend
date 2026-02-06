package com.othertales.modules.writing.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Chapter {

    private UUID id;
    private UUID projectId;
    private String title;
    private String content;
    private int orderIndex;
    private ChapterStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private Chapter() {}

    public static Chapter create(UUID projectId, String title, String content, Integer orderIndex) {
        var chapter = new Chapter();
        chapter.id = UUID.randomUUID();
        chapter.projectId = Objects.requireNonNull(projectId, "Project ID is required");
        chapter.title = (title == null || title.isBlank()) ? "Untitled Chapter" : title.trim();
        chapter.content = content != null ? content : "";
        chapter.orderIndex = orderIndex != null ? orderIndex : 0;
        chapter.status = ChapterStatus.DRAFT;
        chapter.createdAt = Instant.now();
        chapter.updatedAt = chapter.createdAt;
        return chapter;
    }

    public static Chapter reconstitute(
            UUID id,
            UUID projectId,
            String title,
            String content,
            int orderIndex,
            ChapterStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        var chapter = new Chapter();
        chapter.id = id;
        chapter.projectId = projectId;
        chapter.title = title;
        chapter.content = content;
        chapter.orderIndex = orderIndex;
        chapter.status = status;
        chapter.createdAt = createdAt;
        chapter.updatedAt = updatedAt;
        return chapter;
    }

    public void updateTitle(String newTitle) {
        if (newTitle != null && !newTitle.isBlank()) {
            this.title = newTitle.trim();
            this.updatedAt = Instant.now();
        }
    }

    public void updateContent(String newContent) {
        this.content = newContent != null ? newContent : "";
        this.updatedAt = Instant.now();
    }

    public void reorder(int newOrderIndex) {
        this.orderIndex = newOrderIndex;
        this.updatedAt = Instant.now();
    }

    public int getWordCount() {
        if (content == null || content.isBlank()) {
            return 0;
        }
        return content.trim().split("\\s+").length;
    }

    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getOrderIndex() { return orderIndex; }
    public ChapterStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(id, chapter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
