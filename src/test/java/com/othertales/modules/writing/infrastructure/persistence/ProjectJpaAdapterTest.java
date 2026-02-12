package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.domain.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectJpaAdapterTest {

    @Mock
    private ProjectJpaRepository jpaRepository;

    private ProjectMapper mapper;
    private ProjectJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = new ProjectMapper();
        adapter = new ProjectJpaAdapter(jpaRepository, mapper);
    }

    @Test
    void save_should_map_domain_to_entity_and_save() {
        // Given
        UUID userId = UUID.randomUUID();
        Project domainProject = Project.create(userId, "Title", "Synopsis", "Fantasy", 50000);

        // Mock finding existing entity as empty (new project)
        when(jpaRepository.findById(domainProject.getId())).thenReturn(Optional.empty());

        // Use ArgumentCaptor to verify mapping
        org.mockito.ArgumentCaptor<ProjectEntity> entityCaptor = org.mockito.ArgumentCaptor
                .forClass(ProjectEntity.class);

        // Mock save to return what is passed (simulating DB save)
        when(jpaRepository.save(any(ProjectEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Project savedProject = adapter.save(domainProject);

        // Then
        verify(jpaRepository).save(entityCaptor.capture());
        ProjectEntity capturedEntity = entityCaptor.getValue();

        assertThat(capturedEntity.getId()).isEqualTo(domainProject.getId());
        assertThat(capturedEntity.getTitle()).isEqualTo("Title");
        assertThat(capturedEntity.getUserId()).isEqualTo(userId);
        assertThat(capturedEntity.getTargetWordCount()).isEqualTo(50000);

        assertThat(savedProject).isNotNull();
        assertThat(savedProject.getId()).isEqualTo(domainProject.getId());
    }

    @Test
    void findById_should_map_entity_to_domain() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ProjectEntity entity = new ProjectEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setTitle("Stored Title");
        entity.setStatus(ProjectEntity.ProjectStatusEntity.DRAFT);
        entity.setDeleted(false);

        when(jpaRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(entity));

        // When
        Optional<Project> result = adapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Stored Title");
        assertThat(result.get().getId()).isEqualTo(id);
    }
}
