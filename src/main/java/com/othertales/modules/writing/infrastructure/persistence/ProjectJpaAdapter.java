package com.othertales.modules.writing.infrastructure.persistence;

import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.domain.Project;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProjectJpaAdapter implements ProjectRepository {

    private final ProjectJpaRepository jpaRepository;
    private final ProjectMapper mapper;

    public ProjectJpaAdapter(ProjectJpaRepository jpaRepository, ProjectMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Project save(Project project) {
        var entity = mapper.toEntity(project);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpaRepository.findByIdAndDeletedFalse(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Project> findByIdAndUserId(UUID id, UUID userId) {
        return jpaRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Project> findAllByUserId(UUID userId, int page, int size, String sortBy) {
        var sort = buildSort(sortBy);
        var pageable = PageRequest.of(page, size, sort);
        return jpaRepository.findAllByUserIdAndDeletedFalse(userId, pageable)
                .map(mapper::toDomain)
                .getContent();
    }

    @Override
    public long countByUserId(UUID userId) {
        return jpaRepository.countByUserIdAndDeletedFalse(userId);
    }

    @Override
    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return jpaRepository.existsByIdAndUserIdAndDeletedFalse(id, userId);
    }

    private Sort buildSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "updatedAt");
        }

        return switch (sortBy.toLowerCase()) {
            case "title" -> Sort.by(Sort.Direction.ASC, "title");
            case "title_desc" -> Sort.by(Sort.Direction.DESC, "title");
            case "created" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "created_asc" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "updated_asc" -> Sort.by(Sort.Direction.ASC, "updatedAt");
            default -> Sort.by(Sort.Direction.DESC, "updatedAt");
        };
    }
}
