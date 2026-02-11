package com.othertales.modules.writing.application.usecase;

import com.othertales.modules.writing.application.dto.CreateProjectRequest;
import com.othertales.modules.writing.application.dto.ProjectListResponse;
import com.othertales.modules.writing.application.dto.ProjectResponse;
import com.othertales.modules.writing.application.dto.ProjectSummaryResponse;
import com.othertales.modules.writing.application.dto.UpdateProjectRequest;
import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.domain.Project;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import com.othertales.modules.writing.domain.ProjectStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service aggregating all Project CRUD operations.
 * AUDIT FIX #4 (FASE 1.4): Added update endpoint.
 * AUDIT FIX #9 (FASE 2.3): Controller no longer calls ProjectRepository directly.
 * AUDIT FIX #16 (FASE 3.5): All multi-step operations wrapped in @Transactional.
 */
@Service
public class ProjectService {

    private static final int DEFAULT_TARGET_WORD_COUNT = 50000;

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public ProjectResponse create(UUID userId, CreateProjectRequest request) {
        var targetWordCount = request.targetWordCount() != null
                ? request.targetWordCount()
                : DEFAULT_TARGET_WORD_COUNT;

        var project = Project.create(
                userId,
                request.title(),
                request.synopsis(),
                request.genre(),
                targetWordCount
        );

        var saved = projectRepository.save(project);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(UUID projectId, UUID userId) {
        var project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        return toResponse(project);
    }

    @Transactional(readOnly = true)
    public ProjectListResponse listByUser(UUID userId, int page, int size, String sortBy) {
        var safeSize = Math.min(Math.max(size, 1), 100);
        var safePage = Math.max(page, 0);

        var projects = projectRepository.findAllByUserId(userId, safePage, safeSize, sortBy);
        var totalElements = projectRepository.countByUserId(userId);
        var totalPages = (int) Math.ceil((double) totalElements / safeSize);

        var content = projects.stream()
                .map(this::toSummaryResponse)
                .toList();

        return new ProjectListResponse(content, safePage, safeSize, totalElements, totalPages);
    }

    @Transactional
    public ProjectResponse update(UUID projectId, UUID userId, UpdateProjectRequest request) {
        var project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (request.title() != null) {
            project.updateTitle(request.title());
        }
        if (request.synopsis() != null) {
            project.updateSynopsis(request.synopsis());
        }
        if (request.genre() != null) {
            project.updateGenre(request.genre());
        }
        if (request.targetWordCount() != null) {
            project.updateTargetWordCount(request.targetWordCount());
        }
        if (request.coverUrl() != null) {
            project.updateCoverUrl(request.coverUrl());
        }
        if (request.status() != null) {
            var newStatus = ProjectStatus.valueOf(request.status().toUpperCase());
            project.updateStatus(newStatus);
        }

        var saved = projectRepository.save(project);
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID projectId, UUID userId) {
        var project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        project.markAsDeleted();
        projectRepository.save(project);
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getSynopsis(),
                project.getGenre(),
                project.getCurrentWordCount(),
                project.getTargetWordCount(),
                project.getCoverUrl(),
                project.getStatus().name(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    private ProjectSummaryResponse toSummaryResponse(Project project) {
        return new ProjectSummaryResponse(
                project.getId(),
                project.getTitle(),
                project.getGenre(),
                project.getCurrentWordCount(),
                project.getTargetWordCount(),
                project.getCoverUrl(),
                project.getStatus().name(),
                project.getUpdatedAt()
        );
    }
}
