package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.domain.Project;
import com.othertales.modules.writing.domain.ProjectStatus;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectEntity toEntity(Project project) {
        var entity = new ProjectEntity();
        entity.setId(project.getId());
        entity.setUserId(project.getUserId());
        entity.setTitle(project.getTitle());
        entity.setSynopsis(project.getSynopsis());
        entity.setGenre(project.getGenre());
        entity.setCurrentWordCount(project.getCurrentWordCount());
        entity.setTargetWordCount(project.getTargetWordCount());
        entity.setCoverUrl(project.getCoverUrl());
        entity.setStatus(toEntityStatus(project.getStatus()));
        entity.setDeleted(project.isDeleted());
        entity.setCreatedAt(project.getCreatedAt());
        entity.setUpdatedAt(project.getUpdatedAt());
        entity.setVersion(project.getVersion());
        return entity;
    }

    public Project toDomain(ProjectEntity entity) {
        return Project.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getSynopsis(),
                entity.getGenre(),
                entity.getCurrentWordCount(),
                entity.getTargetWordCount(),
                entity.getCoverUrl(),
                toDomainStatus(entity.getStatus()),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    private ProjectEntity.ProjectStatusEntity toEntityStatus(ProjectStatus status) {
        return switch (status) {
            case DRAFT -> ProjectEntity.ProjectStatusEntity.DRAFT;
            case PUBLISHED -> ProjectEntity.ProjectStatusEntity.PUBLISHED;
        };
    }

    private ProjectStatus toDomainStatus(ProjectEntity.ProjectStatusEntity status) {
        return switch (status) {
            case DRAFT -> ProjectStatus.DRAFT;
            case PUBLISHED -> ProjectStatus.PUBLISHED;
        };
    }
}
