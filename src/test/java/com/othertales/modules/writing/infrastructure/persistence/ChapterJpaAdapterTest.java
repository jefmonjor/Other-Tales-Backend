package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.domain.Chapter;
import com.othertales.modules.writing.domain.ChapterStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChapterJpaAdapterTest {

    @Mock
    private ChapterJpaRepository jpaRepository;
    @Mock
    private ProjectJpaRepository projectJpaRepository;

    private ChapterMapper mapper;
    private ChapterJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = new ChapterMapper();
        adapter = new ChapterJpaAdapter(jpaRepository, projectJpaRepository, mapper);
    }

    @Test
    void save_should_map_domain_to_entity_and_save() {
        // Given
        UUID projectId = UUID.randomUUID();
        Chapter chapter = Chapter.create(projectId, "Chapter 1", "", 1);
        chapter.updateContent("Some content");

        // Mock Project reference
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(projectId);
        when(projectJpaRepository.getReferenceById(projectId)).thenReturn(projectEntity);

        // Mock finding existing entity as empty (new)
        when(jpaRepository.findById(chapter.getId())).thenReturn(Optional.empty());

        // Mock save
        when(jpaRepository.save(any(ChapterEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Chapter savedChapter = adapter.save(chapter);

        // Then
        ArgumentCaptor<ChapterEntity> captor = ArgumentCaptor.forClass(ChapterEntity.class);
        verify(jpaRepository).save(captor.capture());

        ChapterEntity captured = captor.getValue();
        assertThat(captured.getId()).isEqualTo(chapter.getId());
        assertThat(captured.getTitle()).isEqualTo("Chapter 1");
        assertThat(captured.getContent()).isEqualTo("Some content");
        assertThat(captured.getOrderIndex()).isEqualTo(1);
        assertThat(captured.getStatus()).isEqualTo(ChapterEntity.ChapterStatusEntity.DRAFT);
        assertThat(captured.getProject().getId()).isEqualTo(projectId);

        assertThat(savedChapter).isNotNull();
    }

    @Test
    void findByProjectIdOrderByOrderIndex_should_return_sorted_chapters() {
        // Given
        UUID projectId = UUID.randomUUID();
        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);

        ChapterEntity entity1 = new ChapterEntity();
        entity1.setId(UUID.randomUUID());
        entity1.setProject(project);
        entity1.setTitle("Ch 1");
        entity1.setOrderIndex(1);
        entity1.setStatus(ChapterEntity.ChapterStatusEntity.PUBLISHED);

        ChapterEntity entity2 = new ChapterEntity();
        entity2.setId(UUID.randomUUID());
        entity2.setProject(project);
        entity2.setTitle("Ch 2");
        entity2.setOrderIndex(2);
        entity2.setStatus(ChapterEntity.ChapterStatusEntity.DRAFT);

        when(jpaRepository.findByProjectIdOrderByOrderIndex(projectId)).thenReturn(List.of(entity1, entity2));

        // When
        List<Chapter> chapters = adapter.findByProjectIdOrderByOrderIndex(projectId);

        // Then
        assertThat(chapters).hasSize(2);
        assertThat(chapters.get(0).getTitle()).isEqualTo("Ch 1");
        assertThat(chapters.get(0).getStatus()).isEqualTo(ChapterStatus.PUBLISHED);
        assertThat(chapters.get(1).getTitle()).isEqualTo("Ch 2");
        assertThat(chapters.get(1).getStatus()).isEqualTo(ChapterStatus.DRAFT);
    }
}
