package com.othertales.modules.writing.application.usecase;

import com.othertales.modules.writing.application.dto.ChapterResponse;
import com.othertales.modules.writing.application.dto.CreateChapterRequest;
import com.othertales.modules.writing.application.dto.UpdateChapterRequest;
import com.othertales.modules.writing.application.port.ChapterRepository;
import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.domain.Chapter;
import com.othertales.modules.writing.domain.ChapterAccessDeniedException;
import com.othertales.modules.writing.domain.Project;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChapterServiceTest {

    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private ProjectRepository projectRepository;

    private ChapterService service;

    @BeforeEach
    void setUp() {
        service = new ChapterService(chapterRepository, projectRepository);
    }

    @Test
    void createChapter_should_save_and_update_word_count() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        CreateChapterRequest request = new CreateChapterRequest("New Chapter", "Content", null);

        when(projectRepository.existsByIdAndUserId(projectId, userId)).thenReturn(true);
        when(chapterRepository.findNextOrderIndex(projectId)).thenReturn(1);
        when(chapterRepository.save(any(Chapter.class))).thenAnswer(i -> i.getArguments()[0]);

        // Mock word count sync
        Project project = Project.create(userId, "Project", "Syn", "FANTASY", 50000);
        when(projectRepository.findByIdAndUserId(projectId, userId)).thenReturn(Optional.of(project));
        when(chapterRepository.findByProjectIdOrderByOrderIndex(projectId)).thenReturn(Collections.emptyList());

        // When
        ChapterResponse response = service.createChapter(projectId, request, userId);

        // Then
        ArgumentCaptor<Chapter> captor = ArgumentCaptor.forClass(Chapter.class);
        verify(chapterRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("New Chapter");

        verify(projectRepository).save(any(Project.class)); // Verifies word count sync
    }

    @Test
    void updateChapter_should_modify_fields_and_sync_word_count() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID chapterId = UUID.randomUUID();

        Chapter chapter = Chapter.create(projectId, "Old Title", "Old Content", 1);
        UpdateChapterRequest request = new UpdateChapterRequest("New Title", "New Content", "PUBLISHED");

        when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));
        when(projectRepository.existsByIdAndUserId(projectId, userId)).thenReturn(true);
        when(chapterRepository.save(any(Chapter.class))).thenAnswer(i -> i.getArguments()[0]);

        // Mock word count sync
        Project project = Project.create(userId, "Project", "Syn", "FANTASY", 50000);
        when(projectRepository.findByIdAndUserId(projectId, userId)).thenReturn(Optional.of(project));
        when(chapterRepository.findByProjectIdOrderByOrderIndex(projectId))
                .thenReturn(Collections.singletonList(chapter));

        // When
        service.updateChapter(chapterId, request, userId);

        // Then
        assertThat(chapter.getTitle()).isEqualTo("New Title");
        assertThat(chapter.getContent()).isEqualTo("New Content");
        assertThat(chapter.getStatus().name()).isEqualTo("PUBLISHED");

        verify(projectRepository).save(project);
    }

    @Test
    void deleteChapter_should_remove_and_sync() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID chapterId = UUID.randomUUID();
        Chapter chapter = Chapter.create(projectId, "Title", "Content", 1);

        when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));
        when(projectRepository.existsByIdAndUserId(projectId, userId)).thenReturn(true);

        // Mock word count sync
        Project project = Project.create(userId, "Project", "Syn", "FANTASY", 50000);
        when(projectRepository.findByIdAndUserId(projectId, userId)).thenReturn(Optional.of(project));
        when(chapterRepository.findByProjectIdOrderByOrderIndex(projectId)).thenReturn(Collections.emptyList());

        // When
        service.deleteChapter(chapterId, userId);

        // Then
        verify(chapterRepository).deleteById(chapterId);
        verify(projectRepository).save(project);
    }

    @Test
    void should_throw_if_project_not_owned() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        CreateChapterRequest request = new CreateChapterRequest("Title", "Content", 1);

        when(projectRepository.existsByIdAndUserId(projectId, userId)).thenReturn(false);

        assertThatThrownBy(() -> service.createChapter(projectId, request, userId))
                .isInstanceOf(ProjectNotFoundException.class);
    }
}
