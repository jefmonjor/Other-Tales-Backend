package com.othertales.modules.writing.application.usecase;

import com.othertales.modules.writing.application.dto.CreateIdeaRequest;
import com.othertales.modules.writing.application.dto.IdeaResponse;
import com.othertales.modules.writing.application.dto.UpdateIdeaRequest;
import com.othertales.modules.writing.application.port.IdeaRepository;
import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.domain.Idea;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final ProjectRepository projectRepository;

    public IdeaService(IdeaRepository ideaRepository, ProjectRepository projectRepository) {
        this.ideaRepository = ideaRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public Page<IdeaResponse> getIdeasByProjectId(UUID projectId, Pageable pageable, UUID userId) {
        verifyProjectOwnership(projectId, userId);
        return ideaRepository.findAllByProjectId(projectId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public IdeaResponse getIdeaById(UUID ideaId, UUID userId) {
        var idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + ideaId));

        verifyProjectOwnership(idea.getProjectId(), userId);
        return toResponse(idea);
    }

    @Transactional
    public IdeaResponse createIdea(UUID projectId, CreateIdeaRequest request, UUID userId) {
        verifyProjectOwnership(projectId, userId);

        var idea = Idea.create(
                projectId,
                request.title(),
                request.content());

        var saved = ideaRepository.save(idea);
        return toResponse(saved);
    }

    @Transactional
    public IdeaResponse updateIdea(UUID ideaId, UpdateIdeaRequest request, UUID userId) {
        var idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + ideaId));

        verifyProjectOwnership(idea.getProjectId(), userId);

        idea.update(
                request.title(),
                request.content());

        var saved = ideaRepository.save(idea);
        return toResponse(saved);
    }

    @Transactional
    public void deleteIdea(UUID ideaId, UUID userId) {
        var idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + ideaId));

        verifyProjectOwnership(idea.getProjectId(), userId);

        idea.markAsDeleted();
        ideaRepository.save(idea);
    }

    private void verifyProjectOwnership(UUID projectId, UUID userId) {
        if (!projectRepository.existsByIdAndUserId(projectId, userId)) {
            throw new ProjectNotFoundException(projectId);
        }
    }

    private IdeaResponse toResponse(Idea idea) {
        return new IdeaResponse(
                idea.getId(),
                idea.getProjectId(),
                idea.getTitle(),
                idea.getContent(),
                idea.getCreatedAt(),
                idea.getUpdatedAt());
    }
}
