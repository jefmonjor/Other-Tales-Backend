package com.othertales.modules.writing.infrastructure.web;

import com.othertales.modules.writing.application.dto.CreateProjectRequest;
import com.othertales.modules.writing.application.dto.ProjectListResponse;
import com.othertales.modules.writing.application.dto.ProjectResponse;
import com.othertales.modules.writing.application.dto.ProjectSummaryResponse;
import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.application.usecase.CreateProjectUseCase;
import com.othertales.modules.writing.domain.Project;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final ProjectRepository projectRepository;

    public ProjectController(
            CreateProjectUseCase createProjectUseCase,
            ProjectRepository projectRepository
    ) {
        this.createProjectUseCase = createProjectUseCase;
        this.projectRepository = projectRepository;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        var command = new CreateProjectUseCase.Command(
                userId,
                request.title(),
                request.synopsis(),
                request.genre(),
                request.targetWordCount()
        );

        var project = createProjectUseCase.execute(command);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(project));
    }

    @GetMapping
    public ResponseEntity<ProjectListResponse> listProjects(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy
    ) {
        var projects = projectRepository.findAllByUserId(userId, page, size, sortBy);
        var totalElements = projectRepository.countByUserId(userId);
        var totalPages = (int) Math.ceil((double) totalElements / size);

        var content = projects.stream()
                .map(this::toSummaryResponse)
                .toList();

        var response = new ProjectListResponse(content, page, size, totalElements, totalPages);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(
            @PathVariable UUID projectId,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        var project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        return ResponseEntity.ok(toResponse(project));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable UUID projectId,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        var project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        project.markAsDeleted();
        projectRepository.save(project);

        return ResponseEntity.noContent().build();
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
                project.getStatus().name(),
                project.getUpdatedAt()
        );
    }
}
