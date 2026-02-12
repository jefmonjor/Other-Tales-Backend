package com.othertales.modules.writing.application.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectDtoTest {

    @Test
    void should_create_and_access_create_project_request() {
        String title = "My Project";
        String synopsis = "A great story";
        String genre = "Fantasy";
        int targetWordCount = 50000;

        CreateProjectRequest request = new CreateProjectRequest(title, synopsis, genre, targetWordCount);

        assertThat(request.title()).isEqualTo(title);
        assertThat(request.synopsis()).isEqualTo(synopsis);
        assertThat(request.genre()).isEqualTo(genre);
        assertThat(request.targetWordCount()).isEqualTo(targetWordCount);

        // Record specific methods
        assertThat(request.toString()).contains(title, genre);
        assertThat(request.hashCode()).isNotZero();

        CreateProjectRequest duplicate = new CreateProjectRequest(title, synopsis, genre, targetWordCount);
        assertThat(request).isEqualTo(duplicate);
    }

    @Test
    void should_create_and_access_project_summary_response() {
        UUID id = UUID.randomUUID();
        String title = "Summary Title";
        String genre = "Fantasy";
        int current = 10;
        int target = 100;
        String coverUrl = "http://cover.url";
        String status = "PUBLISHED";
        java.time.Instant now = java.time.Instant.now();

        ProjectSummaryResponse response = new ProjectSummaryResponse(
                id, title, genre, current, target, coverUrl, status, now);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.title()).isEqualTo(title);
        assertThat(response.genre()).isEqualTo(genre);
        assertThat(response.currentWordCount()).isEqualTo(current);
        assertThat(response.targetWordCount()).isEqualTo(target);
        assertThat(response.coverUrl()).isEqualTo(coverUrl);
        assertThat(response.status()).isEqualTo(status);
        assertThat(response.updatedAt()).isEqualTo(now);

        // Record specific methods
        assertThat(response.toString()).contains(title, status);
        assertThat(response.hashCode()).isNotZero();

        ProjectSummaryResponse duplicate = new ProjectSummaryResponse(
                id, title, genre, current, target, coverUrl, status, now);
        assertThat(response).isEqualTo(duplicate);
    }
}
