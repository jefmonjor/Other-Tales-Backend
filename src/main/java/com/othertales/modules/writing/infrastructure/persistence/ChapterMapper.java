package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.domain.Chapter;
import com.othertales.modules.writing.domain.ChapterStatus;
import org.springframework.stereotype.Component;

@Component
public class ChapterMapper {

    public ChapterEntity toEntity(Chapter chapter, ProjectEntity project) {
        var entity = new ChapterEntity();
        applyToEntity(entity, chapter, project);
        return entity;
    }

    public ChapterEntity toEntity(Chapter chapter, ProjectEntity project, ChapterEntity existingEntity) {
        applyToEntity(existingEntity, chapter, project);
        return existingEntity;
    }

    private void applyToEntity(ChapterEntity entity, Chapter chapter, ProjectEntity project) {
        entity.setId(chapter.getId());
        entity.setProject(project);
        entity.setTitle(chapter.getTitle());
        entity.setContent(chapter.getContent());
        entity.setOrderIndex(chapter.getOrderIndex());
        entity.setStatus(toEntityStatus(chapter.getStatus()));
        entity.setCreatedAt(chapter.getCreatedAt());
        entity.setUpdatedAt(chapter.getUpdatedAt());
    }

    public Chapter toDomain(ChapterEntity entity) {
        return Chapter.reconstitute(
                entity.getId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getOrderIndex(),
                toDomainStatus(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private ChapterEntity.ChapterStatusEntity toEntityStatus(ChapterStatus status) {
        return switch (status) {
            case DRAFT -> ChapterEntity.ChapterStatusEntity.DRAFT;
            case PUBLISHED -> ChapterEntity.ChapterStatusEntity.PUBLISHED;
        };
    }

    private ChapterStatus toDomainStatus(ChapterEntity.ChapterStatusEntity status) {
        return switch (status) {
            case DRAFT -> ChapterStatus.DRAFT;
            case PUBLISHED -> ChapterStatus.PUBLISHED;
        };
    }
}
