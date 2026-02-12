package com.othertales.modules.writing.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StoryJpaRepository extends JpaRepository<StoryEntity, UUID> {

    @Query("SELECT s FROM StoryEntity s WHERE s.project.id = :projectId AND s.deleted = false")
    Page<StoryEntity> findByProjectId(@Param("projectId") UUID projectId, Pageable pageable);

    @Query("SELECT s FROM StoryEntity s WHERE s.id = :id AND s.project.id = :projectId AND s.deleted = false")
    Optional<StoryEntity> findByIdAndProjectId(@Param("id") UUID id, @Param("projectId") UUID projectId);
}
