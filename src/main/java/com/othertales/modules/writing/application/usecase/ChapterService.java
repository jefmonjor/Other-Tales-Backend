package com.othertales.modules.writing.application.usecase;

import com.othertales.modules.writing.application.dto.ChapterResponse;
import com.othertales.modules.writing.application.dto.CreateChapterRequest;
import com.othertales.modules.writing.application.dto.UpdateChapterRequest;
import com.othertales.modules.writing.application.port.ChapterRepository;
import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.domain.Chapter;
import com.othertales.modules.writing.domain.ChapterAccessDeniedException;
import com.othertales.modules.writing.domain.ChapterNotFoundException;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * AUDIT FIX #6 (FASE 2.1): Fully decoupled from infrastructure. Uses only domain
 * objects and port interfaces. No JPA entity or repository imports.
 * AUDIT FIX #5 (FASE 1.5): Separate create/update/delete/get methods per OpenAPI spec.
 * AUDIT FIX #15 (FASE 3.5): @Transactional on all operations.
 */
@Service
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final ProjectRepository projectRepository;

    public ChapterService(ChapterRepository chapterRepository, ProjectRepository projectRepository) {
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

    @Transactional(readOnly = true)
    public ChapterResponse getChapterById(UUID chapterId, UUID userId) {
        var chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ChapterNotFoundException(chapterId));

        verifyProjectOwnership(chapter.getProjectId(), userId);
        return toResponse(chapter);
    }

    @Transactional
    public ChapterResponse createChapter(UUID projectId, CreateChapterRequest request, UUID userId) {
        if (!projectRepository.existsByIdAndUserId(projectId, userId)) {
            throw new ProjectNotFoundException(projectId);
        }

        var orderIndex = request.sortOrder() != null
                ? request.sortOrder()
                : chapterRepository.findNextOrderIndex(projectId);

        var chapter = Chapter.create(projectId, request.title(), request.content(), orderIndex);
        var saved = chapterRepository.save(chapter);
        return toResponse(saved);
    }

    @Transactional
    public ChapterResponse updateChapter(UUID chapterId, UpdateChapterRequest request, UUID userId) {
        var chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ChapterNotFoundException(chapterId));

        verifyProjectOwnership(chapter.getProjectId(), userId);

        if (request.title() != null) {
            chapter.updateTitle(request.title());
        }
        if (request.content() != null) {
            chapter.updateContent(request.content());
        }

        var saved = chapterRepository.save(chapter);
        return toResponse(saved);
    }

    @Transactional
    public void deleteChapter(UUID chapterId, UUID userId) {
        var chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ChapterNotFoundException(chapterId));

        verifyProjectOwnership(chapter.getProjectId(), userId);
        chapterRepository.deleteById(chapterId);
    }

    private void verifyProjectOwnership(UUID projectId, UUID userId) {
        if (!projectRepository.existsByIdAndUserId(projectId, userId)) {
            throw new ChapterAccessDeniedException(projectId, userId);
        }
    }

    private ChapterResponse toResponse(Chapter chapter) {
        return new ChapterResponse(
                chapter.getId(),
                chapter.getProjectId(),
                chapter.getTitle(),
                chapter.getContent(),
                chapter.getOrderIndex(),
                chapter.getWordCount(),
                chapter.getCreatedAt(),
                chapter.getUpdatedAt()
        );
    }
}
