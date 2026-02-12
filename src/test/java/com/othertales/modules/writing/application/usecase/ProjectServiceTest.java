package com.othertales.modules.writing.application.usecase;

import com.othertales.modules.writing.application.dto.CreateProjectRequest;
import com.othertales.modules.writing.application.dto.ProjectListResponse;
import com.othertales.modules.writing.application.dto.ProjectResponse;
import com.othertales.modules.writing.application.dto.UpdateProjectRequest;
import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.domain.Project;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void create_should_save_and_return_project() {
        // Given
        UUID userId = UUID.randomUUID();
        CreateProjectRequest request = new CreateProjectRequest(
                "My Novel",
                "Synopsis",
                "Fantasy",
                50000);
        Project savedProject = Project.create(userId, request.title(), request.synopsis(), request.genre(),
                request.targetWordCount());

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        // When
        ProjectResponse response = projectService.create(userId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo(request.title());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void list_should_return_all_projects() {
        // Given
        UUID userId = UUID.randomUUID();
        Project p1 = Project.create(userId, "P1", "S1", "G1", 1000);
        Project p2 = Project.create(userId, "P2", "S2", "G2", 2000);

        when(projectRepository.findAllByUserId(userId, 0, 10, "updatedAt,desc"))
                .thenReturn(List.of(p1, p2));
        when(projectRepository.countByUserId(userId)).thenReturn(2L);

        // When
        ProjectListResponse response = projectService.listByUser(userId, 0, 10, "updatedAt,desc");

        // Then
        assertThat(response.content()).hasSize(2);
        assertThat(response.totalElements()).isEqualTo(2);
    }

    @Test
    void update_should_modify_existing_project() {
        // Given
        UUID userId = UUID.randomUUID();
        Project project = Project.create(userId, "Old Title", "Syn", "Gen", 1000);
        UUID projectId = project.getId();
        UpdateProjectRequest request = new UpdateProjectRequest(
                "New Title",
                null,
                null,
                null,
                null,
                null);

        when(projectRepository.findByIdAndUserId(projectId, userId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // When
        ProjectResponse response = projectService.update(projectId, userId, request);

        // Then
        assertThat(response.title()).isEqualTo("New Title");
        verify(projectRepository).save(project);
    }

    @Test
    void update_should_throw_when_not_found() {
        // Given
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UpdateProjectRequest request = new UpdateProjectRequest("Title", null, null, null, null, null);

        when(projectRepository.findByIdAndUserId(projectId, userId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> projectService.update(projectId, userId, request))
                .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void delete_should_mark_as_deleted() {
        // Given
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Project project = Project.create(userId, "Title", "Syn", "Gen", 1000);

        when(projectRepository.findByIdAndUserId(projectId, userId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // When
        projectService.delete(projectId, userId);

        // Then
        assertThat(project.isDeleted()).isTrue();
        verify(projectRepository).save(project);
    }
}
