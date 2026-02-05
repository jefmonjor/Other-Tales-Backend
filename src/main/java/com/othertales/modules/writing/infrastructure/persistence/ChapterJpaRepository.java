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

    @Query("SELECT c FROM ChapterEntity c WHERE c.id = :id AND c.project.id = :projectId")
    Optional<ChapterEntity> findByIdAndProjectId(@Param("id") UUID id, @Param("projectId") UUID projectId);

    @Query("SELECT COALESCE(MAX(c.orderIndex), -1) + 1 FROM ChapterEntity c WHERE c.project.id = :projectId")
    int findNextOrderIndex(@Param("projectId") UUID projectId);

    @Query("SELECT COUNT(c) FROM ChapterEntity c WHERE c.project.id = :projectId")
    long countByProjectId(@Param("projectId") UUID projectId);
}
