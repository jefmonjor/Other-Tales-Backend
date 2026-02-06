package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.application.port.ChapterRepository;
import com.othertales.modules.writing.domain.Chapter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ChapterJpaAdapter implements ChapterRepository {

    private final ChapterJpaRepository jpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final ChapterMapper mapper;

    public ChapterJpaAdapter(
            ChapterJpaRepository jpaRepository,
            ProjectJpaRepository projectJpaRepository,
            ChapterMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.projectJpaRepository = projectJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Chapter save(Chapter chapter) {
        var project = projectJpaRepository.getReferenceById(chapter.getProjectId());
        var entity = mapper.toEntity(chapter, project);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Chapter> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Chapter> findByIdAndProjectId(UUID id, UUID projectId) {
        return jpaRepository.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public List<Chapter> findByProjectIdOrderByOrderIndex(UUID projectId) {
        return jpaRepository.findByProjectIdOrderByOrderIndex(projectId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public int findNextOrderIndex(UUID projectId) {
        return jpaRepository.findNextOrderIndex(projectId);
    }

    @Override
    public long countByProjectId(UUID projectId) {
        return jpaRepository.countByProjectId(projectId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
