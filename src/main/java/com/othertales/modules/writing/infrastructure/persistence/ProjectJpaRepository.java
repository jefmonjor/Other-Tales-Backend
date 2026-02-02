package com.othertales.modules.writing.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, UUID> {

    Optional<ProjectEntity> findByIdAndUserIdAndDeletedFalse(UUID id, UUID userId);

    Optional<ProjectEntity> findByIdAndDeletedFalse(UUID id);

    Page<ProjectEntity> findAllByUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    long countByUserIdAndDeletedFalse(UUID userId);

    boolean existsByIdAndUserIdAndDeletedFalse(UUID id, UUID userId);
}
