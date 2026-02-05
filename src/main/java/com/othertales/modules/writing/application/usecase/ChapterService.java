package com.othertales.modules.writing.application.usecase;

import com.othertales.modules.writing.application.dto.ChapterResponse;
import com.othertales.modules.writing.application.dto.SaveChapterRequest;
import com.othertales.modules.writing.domain.ChapterAccessDeniedException;
import com.othertales.modules.writing.domain.ChapterNotFoundException;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import com.othertales.modules.writing.infrastructure.persistence.ChapterEntity;
import com.othertales.modules.writing.infrastructure.persistence.ChapterJpaRepository;
import com.othertales.modules.writing.infrastructure.persistence.ProjectJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ChapterService {

    private final ChapterJpaRepository chapterRepository;
    private final ProjectJpaRepository projectRepository;

    public ChapterService(
            ChapterJpaRepository chapterRepository,
            ProjectJpaRepository projectRepository
    ) {
        this.chapterRepository = chapterRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ChapterResponse> getChaptersByProjectId(UUID projectId, UUID userId) {
        verifyProjectOwnership(projectId, userId);

        return chapterRepository.findByProjectIdOrderByOrderIndex(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ChapterResponse createOrUpdateChapter(SaveChapterRequest request, UUID userId) {
        var projectId = request.projectId();
        var project = projectRepository.findByIdAndDeletedFalse(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        // SEGURIDAD: Verificar que el usuario es dueño del proyecto
        if (!project.getUserId().equals(userId)) {
            throw new ChapterAccessDeniedException(projectId, userId);
        }

        ChapterEntity chapter;
        var now = Instant.now();

        if (request.id() != null) {
            // Actualizar capítulo existente
            chapter = chapterRepository.findById(request.id())
                    .orElseThrow(() -> new ChapterNotFoundException(request.id()));

            // Verificar que el capítulo pertenece al proyecto
            if (!chapter.getProjectId().equals(projectId)) {
                throw new ChapterAccessDeniedException(projectId, userId);
            }

            chapter.setTitle(request.title());
            chapter.setContent(request.content());
            if (request.orderIndex() != null) {
                chapter.setOrderIndex(request.orderIndex());
            }
            chapter.setUpdatedAt(now);
        } else {
            // Crear nuevo capítulo
            chapter = new ChapterEntity();
            chapter.setId(UUID.randomUUID());
            chapter.setProject(project);
            chapter.setTitle(request.title());
            chapter.setContent(request.content());
            chapter.setOrderIndex(request.orderIndex() != null
                    ? request.orderIndex()
                    : chapterRepository.findNextOrderIndex(projectId));
            chapter.setStatus("DRAFT");
            chapter.setCreatedAt(now);
            chapter.setUpdatedAt(now);
        }

        var saved = chapterRepository.save(chapter);
        return toResponse(saved);
    }

    private void verifyProjectOwnership(UUID projectId, UUID userId) {
        var exists = projectRepository.existsByIdAndUserIdAndDeletedFalse(projectId, userId);
        if (!exists) {
            throw new ChapterAccessDeniedException(projectId, userId);
        }
    }

    private ChapterResponse toResponse(ChapterEntity entity) {
        return new ChapterResponse(
                entity.getId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getOrderIndex(),
                countWords(entity.getContent()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private int countWords(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }
        return content.trim().split("\\s+").length;
    }
}
