package com.othertales.modules.writing.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CharacterJpaRepository extends JpaRepository<CharacterEntity, UUID> {

    @Query("SELECT c FROM CharacterEntity c WHERE c.project.id = :projectId AND c.deleted = false")
    Page<CharacterEntity> findByProjectId(@Param("projectId") UUID projectId, Pageable pageable);

    @Query("SELECT c FROM CharacterEntity c WHERE c.id = :id AND c.project.id = :projectId AND c.deleted = false")
    Optional<CharacterEntity> findByIdAndProjectId(@Param("id") UUID id, @Param("projectId") UUID projectId);
}
