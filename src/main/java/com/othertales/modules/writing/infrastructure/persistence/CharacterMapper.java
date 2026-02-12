package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.domain.Character;
import org.springframework.stereotype.Component;

@Component
public class CharacterMapper {

    public CharacterEntity toEntity(Character character, ProjectEntity project) {
        var entity = new CharacterEntity();
        applyToEntity(entity, character, project);
        return entity;
    }

    public CharacterEntity toEntity(Character character, ProjectEntity project, CharacterEntity existingEntity) {
        applyToEntity(existingEntity, character, project);
        return existingEntity;
    }

    private void applyToEntity(CharacterEntity entity, Character character, ProjectEntity project) {
        entity.setId(character.getId());
        entity.setProject(project);
        entity.setName(character.getName());
        entity.setRole(character.getRole());
        entity.setDescription(character.getDescription());
        entity.setPhysicalDescription(character.getPhysicalDescription());
        entity.setImageUrl(character.getImageUrl());
        entity.setCreatedAt(character.getCreatedAt());
        entity.setUpdatedAt(character.getUpdatedAt());
        entity.setDeleted(character.isDeleted());
    }

    public Character toDomain(CharacterEntity entity) {
        return Character.reconstitute(
                entity.getId(),
                entity.getProjectId(),
                entity.getName(),
                entity.getRole(),
                entity.getDescription(),
                entity.getPhysicalDescription(),
                entity.getImageUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isDeleted());
    }
}
