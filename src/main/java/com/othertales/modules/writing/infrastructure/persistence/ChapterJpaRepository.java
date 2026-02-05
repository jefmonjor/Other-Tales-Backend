package com.othertales.modules.writing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChapterJpaRepository extends JpaRepository<ChapterEntity, UUID> {

    @Query("SELECT c FROM ChapterEntity c WHERE c.project.id = :projectId ORDER BY c.orderIndex ASC")
    List<ChapterEntity> findByProjectIdOrderByOrderIndex(@Param("projectId") UUID projectId);

    Optional<ChapterEntity> findByIdAndProjectId(UUID id, UUID projectId);

    @Query("SELECT COALESCE(MAX(c.orderIndex), -1) + 1 FROM ChapterEntity c WHERE c.project.id = :projectId")
    int findNextOrderIndex(@Param("projectId") UUID projectId);

    long countByProjectId(UUID projectId);
}
