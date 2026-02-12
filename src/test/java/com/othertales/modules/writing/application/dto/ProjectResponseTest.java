package com.othertales.modules.writing.application.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectResponseTest {

    @Test
    void should_create_and_access_record_components() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        ProjectResponse response = new ProjectResponse(
                id, "Title", "Synopsis", "Genre", 100, 50000,
                "Url", "DRAFT", now, now);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.title()).isEqualTo("Title");
        assertThat(response.synopsis()).isEqualTo("Synopsis");
        assertThat(response.genre()).isEqualTo("Genre");
        assertThat(response.currentWordCount()).isEqualTo(100);
        assertThat(response.targetWordCount()).isEqualTo(50000);
        assertThat(response.coverUrl()).isEqualTo("Url");
        assertThat(response.status()).isEqualTo("DRAFT");
        assertThat(response.createdAt()).isEqualTo(now);
        assertThat(response.updatedAt()).isEqualTo(now);

        assertThat(response.toString()).contains("Title", "Synopsis");

        ProjectResponse same = new ProjectResponse(
                id, "Title", "Synopsis", "Genre", 100, 50000,
                "Url", "DRAFT", now, now);
        assertThat(response).isEqualTo(same);
        assertThat(response.hashCode()).isEqualTo(same.hashCode());
    }
}
