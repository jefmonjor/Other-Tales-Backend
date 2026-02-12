package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.infrastructure.persistence.ChapterEntity.ChapterStatusEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChapterEntityTest {

    @Test
    void should_test_all_getters_setters_and_lifecycle() {
        UUID id = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Instant now = Instant.now();

        ChapterEntity entity = new ChapterEntity();
        entity.setId(id);
        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);
        entity.setProject(project); // Set relationship

        entity.setTitle("Chapter 1");
        entity.setContent("Content");
        entity.setOrderIndex(1); // was chapterOrder
        entity.setStatus(ChapterStatusEntity.DRAFT);
        // publishedAt does not exist in entity
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setVersion(1L);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getProjectId()).isEqualTo(projectId); // Uses helper method
        assertThat(entity.getTitle()).isEqualTo("Chapter 1");
        assertThat(entity.getContent()).isEqualTo("Content");
        assertThat(entity.getOrderIndex()).isEqualTo(1);
        assertThat(entity.getStatus()).isEqualTo(ChapterStatusEntity.DRAFT);

        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
        assertThat(entity.getVersion()).isEqualTo(1L);
        assertThat(entity.isNew()).isTrue();

        entity.markNotNew();
        assertThat(entity.isNew()).isFalse();
    }
}
