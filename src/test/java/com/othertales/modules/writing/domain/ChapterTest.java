package com.othertales.modules.writing.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChapterTest {

    @Test
    void should_create_draft() {
        UUID projectId = UUID.randomUUID();
        Chapter chapter = Chapter.create(projectId, "Chapter 1", "", 1);

        assertThat(chapter.getStatus()).isEqualTo(ChapterStatus.DRAFT);
        assertThat(chapter.getContent()).isEmpty();
        assertThat(chapter.getWordCount()).isZero();
        assertThat(chapter.getTitle()).isEqualTo("Chapter 1");
    }

    @Test
    void should_update_content() {
        Chapter chapter = Chapter.create(UUID.randomUUID(), "Title", "Initial", 1);

        chapter.updateContent("Hola mundo");

        assertThat(chapter.getContent()).isEqualTo("Hola mundo");
        assertThat(chapter.getWordCount()).isEqualTo(2);
        assertThat(chapter.getUpdatedAt()).isAfter(chapter.getCreatedAt());
    }

    @Test
    void should_publish() {
        Chapter chapter = Chapter.create(UUID.randomUUID(), "Title", "Content", 1);

        chapter.publish();

        assertThat(chapter.getStatus()).isEqualTo(ChapterStatus.PUBLISHED);
        assertThat(chapter.getPublishedAt()).isNotNull();
    }

    @Test
    void should_calculate_word_count() {
        Chapter chapter = Chapter.create(UUID.randomUUID(), "Title", "", 1);

        chapter.updateContent("One\nTwo  Three");
        assertThat(chapter.getWordCount()).isEqualTo(3);

        chapter.updateContent("   ");
        assertThat(chapter.getWordCount()).isZero();

        chapter.updateContent("SingleWord");
        assertThat(chapter.getWordCount()).isEqualTo(1);
    }
}
