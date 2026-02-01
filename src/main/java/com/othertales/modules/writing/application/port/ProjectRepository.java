package com.othertales.modules.writing.application.port;

import com.othertales.modules.writing.domain.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(UUID id);

    Optional<Project> findByIdAndUserId(UUID id, UUID userId);

    List<Project> findAllByUserId(UUID userId, int page, int size, String sortBy);

    long countByUserId(UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}
