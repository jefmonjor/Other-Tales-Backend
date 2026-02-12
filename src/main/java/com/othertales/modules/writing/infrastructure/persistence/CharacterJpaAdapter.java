package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.application.port.CharacterRepository;
import com.othertales.modules.writing.domain.Character;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CharacterJpaAdapter implements CharacterRepository {

    private final CharacterJpaRepository jpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final CharacterMapper mapper;

    public CharacterJpaAdapter(
            CharacterJpaRepository jpaRepository,
            ProjectJpaRepository projectJpaRepository,
            CharacterMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.projectJpaRepository = projectJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Character save(Character character) {
        var project = projectJpaRepository.getReferenceById(character.getProjectId());
        var existingEntity = jpaRepository.findById(character.getId()).orElse(null);
        var entity = existingEntity != null
                ? mapper.toEntity(character, project, existingEntity)
                : mapper.toEntity(character, project);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Character> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> !e.isDeleted())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Character> findByIdAndProjectId(UUID id, UUID projectId) {
        return jpaRepository.findByIdAndProjectId(id, projectId)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Character> findAllByProjectId(UUID projectId, Pageable pageable) {
        return jpaRepository.findByProjectId(projectId, pageable)
                .map(mapper::toDomain);
    }
}
