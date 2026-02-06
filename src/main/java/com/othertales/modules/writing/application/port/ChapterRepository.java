package com.othertales.modules.writing.application.port;

import com.othertales.modules.writing.domain.Chapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChapterRepository {

    Chapter save(Chapter chapter);

    Optional<Chapter> findById(UUID id);

    Optional<Chapter> findByIdAndProjectId(UUID id, UUID projectId);

    List<Chapter> findByProjectIdOrderByOrderIndex(UUID projectId);

    int findNextOrderIndex(UUID projectId);

    long countByProjectId(UUID projectId);

    void deleteById(UUID id);
}
