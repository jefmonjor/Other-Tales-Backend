package com.othertales.modules.writing.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Story {

    private UUID id;
    private UUID projectId;
    private String title;
    private String synopsis;
    private String theme;
    private String secondaryPlots;
    private String others;
    private String imageUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean deleted;

    private Story() {
    }

    public static Story create(UUID projectId, String title, String synopsis, String theme, String secondaryPlots,
            String others, String imageUrl) {
        var story = new Story();
        story.id = UUID.randomUUID();
        story.projectId = Objects.requireNonNull(projectId, "Project ID is required");
        story.title = Objects.requireNonNull(title, "Title is required").trim();
        if (story.title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        story.synopsis = synopsis != null ? synopsis.trim() : null;
        story.theme = theme != null ? theme.trim() : null;
        story.secondaryPlots = secondaryPlots != null ? secondaryPlots.trim() : null;
        story.others = others != null ? others.trim() : null;
        story.imageUrl = imageUrl != null ? imageUrl.trim() : null;
        story.createdAt = Instant.now();
        story.updatedAt = story.createdAt;
        story.deleted = false;
        return story;
    }

    public static Story reconstitute(
            UUID id,
            UUID projectId,
            String title,
            String synopsis,
            String theme,
            String secondaryPlots,
            String others,
            String imageUrl,
            Instant createdAt,
            Instant updatedAt,
            boolean deleted) {
        var story = new Story();
        story.id = id;
        story.projectId = projectId;
        story.title = title;
        story.synopsis = synopsis;
        story.theme = theme;
        story.secondaryPlots = secondaryPlots;
        story.others = others;
        story.imageUrl = imageUrl;
        story.createdAt = createdAt;
        story.updatedAt = updatedAt;
        story.deleted = deleted;
        return story;
    }

    public void update(String title, String synopsis, String theme, String secondaryPlots, String others,
            String imageUrl) {
        if (title != null) {
            if (title.isBlank()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }
            this.title = title.trim();
        }
        if (synopsis != null)
            this.synopsis = synopsis.trim();
        if (theme != null)
            this.theme = theme.trim();
        if (secondaryPlots != null)
            this.secondaryPlots = secondaryPlots.trim();
        if (others != null)
            this.others = others.trim();
        if (imageUrl != null)
            this.imageUrl = imageUrl.trim();

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

    public String getSynopsis() {
        return synopsis;
    }

    public String getTheme() {
        return theme;
    }

    public String getSecondaryPlots() {
        return secondaryPlots;
    }

    public String getOthers() {
        return others;
    }

    public String getImageUrl() {
        return imageUrl;
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
        Story story = (Story) o;
        return Objects.equals(id, story.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
