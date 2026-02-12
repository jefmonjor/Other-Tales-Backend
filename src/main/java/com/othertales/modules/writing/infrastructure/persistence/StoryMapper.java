package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.domain.Story;
import org.springframework.stereotype.Component;

@Component
public class StoryMapper {

    public StoryEntity toEntity(Story story, ProjectEntity project) {
        var entity = new StoryEntity();
        applyToEntity(entity, story, project);
        return entity;
    }

    public StoryEntity toEntity(Story story, ProjectEntity project, StoryEntity existingEntity) {
        applyToEntity(existingEntity, story, project);
        return existingEntity;
    }

    private void applyToEntity(StoryEntity entity, Story story, ProjectEntity project) {
        entity.setId(story.getId());
        entity.setProject(project);
        entity.setTitle(story.getTitle());
        entity.setSynopsis(story.getSynopsis());
        entity.setTheme(story.getTheme());
        entity.setSecondaryPlots(story.getSecondaryPlots());
        entity.setOthers(story.getOthers());
        entity.setImageUrl(story.getImageUrl());
        entity.setCreatedAt(story.getCreatedAt());
        entity.setUpdatedAt(story.getUpdatedAt());
        entity.setDeleted(story.isDeleted());
    }

    public Story toDomain(StoryEntity entity) {
        return Story.reconstitute(
                entity.getId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.getSynopsis(),
                entity.getTheme(),
                entity.getSecondaryPlots(),
                entity.getOthers(),
                entity.getImageUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isDeleted());
    }
}
