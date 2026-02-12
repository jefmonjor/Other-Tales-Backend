package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.application.port.IdeaRepository;
import com.othertales.modules.writing.domain.Idea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class IdeaJpaAdapter implements IdeaRepository {

    private final IdeaJpaRepository jpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final IdeaMapper mapper;

    public IdeaJpaAdapter(
            IdeaJpaRepository jpaRepository,
            ProjectJpaRepository projectJpaRepository,
            IdeaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.projectJpaRepository = projectJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Idea save(Idea idea) {
        var project = projectJpaRepository.getReferenceById(idea.getProjectId());
        var existingEntity = jpaRepository.findById(idea.getId()).orElse(null);
        var entity = existingEntity != null
                ? mapper.toEntity(idea, project, existingEntity)
                : mapper.toEntity(idea, project);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Idea> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> !e.isDeleted())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Idea> findByIdAndProjectId(UUID id, UUID projectId) {
        return jpaRepository.findByIdAndProjectId(id, projectId)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Idea> findAllByProjectId(UUID projectId, Pageable pageable) {
        return jpaRepository.findByProjectId(projectId, pageable)
                .map(mapper::toDomain);
    }
}
