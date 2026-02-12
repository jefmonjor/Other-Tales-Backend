package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.infrastructure.persistence.ProjectEntity.ProjectStatusEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectEntityTest {

    @Test
    void should_test_all_getters_setters_and_constructors() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();

        ProjectEntity entity = new ProjectEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setTitle("Title");
        entity.setSynopsis("Synopsis");
        entity.setGenre("Genre");
        entity.setCurrentWordCount(100);
        entity.setTargetWordCount(50000);
        entity.setCoverUrl("http://url.com");
        entity.setStatus(ProjectStatusEntity.PUBLISHED);
        entity.setDeleted(true);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        // Version is usually managed by JPA but setter might exist or lombok generates
        // it depending on config, but field exists.
        // Assuming lombok @Setter is on class, so it should exist.

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getTitle()).isEqualTo("Title");
        assertThat(entity.getSynopsis()).isEqualTo("Synopsis");
        assertThat(entity.getGenre()).isEqualTo("Genre");
        assertThat(entity.getCurrentWordCount()).isEqualTo(100);
        assertThat(entity.getTargetWordCount()).isEqualTo(50000);
        assertThat(entity.getCoverUrl()).isEqualTo("http://url.com");
        assertThat(entity.getStatus()).isEqualTo(ProjectStatusEntity.PUBLISHED);
        assertThat(entity.isDeleted()).isTrue();
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
        assertThat(entity.isNew()).isTrue(); // Default is true logic in entity
    }

    @Test
    void should_test_is_new_logic() {
        ProjectEntity entity = new ProjectEntity();
        assertThat(entity.isNew()).isTrue();

        entity.markNotNew();
        assertThat(entity.isNew()).isFalse();
    }
}
