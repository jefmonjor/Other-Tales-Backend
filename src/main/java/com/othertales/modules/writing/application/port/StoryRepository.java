package com.othertales.modules.writing.application.port;

import com.othertales.modules.writing.domain.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface StoryRepository {

    Story save(Story story);

    Optional<Story> findById(UUID id);

    Optional<Story> findByIdAndProjectId(UUID id, UUID projectId);

    Page<Story> findAllByProjectId(UUID projectId, Pageable pageable);
}
