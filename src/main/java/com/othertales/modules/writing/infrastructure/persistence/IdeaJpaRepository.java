package com.othertales.modules.writing.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IdeaJpaRepository extends JpaRepository<IdeaEntity, UUID> {

    @Query("SELECT i FROM IdeaEntity i WHERE i.project.id = :projectId AND i.deleted = false")
    Page<IdeaEntity> findByProjectId(@Param("projectId") UUID projectId, Pageable pageable);

    @Query("SELECT i FROM IdeaEntity i WHERE i.id = :id AND i.project.id = :projectId AND i.deleted = false")
    Optional<IdeaEntity> findByIdAndProjectId(@Param("id") UUID id, @Param("projectId") UUID projectId);
}
