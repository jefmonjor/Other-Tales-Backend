package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.domain.Idea;
import org.springframework.stereotype.Component;

@Component
public class IdeaMapper {

    public IdeaEntity toEntity(Idea idea, ProjectEntity project) {
        var entity = new IdeaEntity();
        applyToEntity(entity, idea, project);
        return entity;
    }

    public IdeaEntity toEntity(Idea idea, ProjectEntity project, IdeaEntity existingEntity) {
        applyToEntity(existingEntity, idea, project);
        return existingEntity;
    }

    private void applyToEntity(IdeaEntity entity, Idea idea, ProjectEntity project) {
        entity.setId(idea.getId());
        entity.setProject(project);
        entity.setTitle(idea.getTitle());
        entity.setContent(idea.getContent());
        entity.setCreatedAt(idea.getCreatedAt());
        entity.setUpdatedAt(idea.getUpdatedAt());
        entity.setDeleted(idea.isDeleted());
    }

    public Idea toDomain(IdeaEntity entity) {
        return Idea.reconstitute(
                entity.getId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isDeleted());
    }
}
