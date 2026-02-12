package com.othertales.modules.writing.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTest {

    @Test
    void should_create_project_with_defaults() {
        // Given
        UUID userId = UUID.randomUUID();
        String title = "My New Novel";
        String synopsis = "A story about testing.";
        String genre = "Sci-Fi";
        int targetWordCount = 50000;

        // When
        Project project = Project.create(userId, title, synopsis, genre, targetWordCount);

        // Then
        assertThat(project.getId()).isNotNull();
        assertThat(project.getUserId()).isEqualTo(userId);
        assertThat(project.getTitle()).isEqualTo(title);
        assertThat(project.getSynopsis()).isEqualTo(synopsis);
        assertThat(project.getGenre()).isEqualTo(genre);
        assertThat(project.getTargetWordCount()).isEqualTo(targetWordCount);
        assertThat(project.getCurrentWordCount()).isZero();
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.DRAFT);
        assertThat(project.getCoverUrl()).isNull();
        assertThat(project.isDeleted()).isFalse();
        assertThat(project.getCreatedAt()).isNotNull();
        assertThat(project.getUpdatedAt()).isEqualTo(project.getCreatedAt());
        assertThat(project.getVersion()).isZero();
    }

    @Test
    void should_update_title() throws InterruptedException {
        // Given
        Project project = Project.create(UUID.randomUUID(), "Old Title", "Syn", "genre", 1000);
        Instant originalUpdatedAt = project.getUpdatedAt();

        // Ensure time passes for assertion
        Thread.sleep(1);

        // When
        project.updateTitle("New Title");

        // Then
        assertThat(project.getTitle()).isEqualTo("New Title");
        assertThat(project.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void should_mark_as_deleted() {
        // Given
        Project project = Project.create(UUID.randomUUID(), "Title", "Syn", "genre", 1000);

        // When
        project.markAsDeleted();

        // Then
        assertThat(project.isDeleted()).isTrue();
    }

    @Test
    void should_reconstitute_from_db() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String title = "Restored Title";
        String synopsis = "Restored Synopsis";
        String genre = "Fantasy";
        int currentWordCount = 1234;
        int targetWordCount = 60000;
        String coverUrl = "http://example.com/cover.jpg";
        ProjectStatus status = ProjectStatus.PUBLISHED;
        boolean deleted = true;
        Instant createdAt = Instant.now().minusSeconds(100);
        Instant updatedAt = Instant.now().minusSeconds(10);
        Long version = 5L;

        // When
        Project project = Project.reconstitute(
                id, userId, title, synopsis, genre, currentWordCount, targetWordCount,
                coverUrl, status, deleted, createdAt, updatedAt, version);

        // Then
        assertThat(project.getId()).isEqualTo(id);
        assertThat(project.getUserId()).isEqualTo(userId);
        assertThat(project.getTitle()).isEqualTo(title);
        assertThat(project.getSynopsis()).isEqualTo(synopsis);
        assertThat(project.getGenre()).isEqualTo(genre);
        assertThat(project.getCurrentWordCount()).isEqualTo(currentWordCount);
        assertThat(project.getTargetWordCount()).isEqualTo(targetWordCount);
        assertThat(project.getCoverUrl()).isEqualTo(coverUrl);
        assertThat(project.getStatus()).isEqualTo(status);
        assertThat(project.isDeleted()).isTrue();
        assertThat(project.getCreatedAt()).isEqualTo(createdAt);
        assertThat(project.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(project.getVersion()).isEqualTo(version);
    }
}
