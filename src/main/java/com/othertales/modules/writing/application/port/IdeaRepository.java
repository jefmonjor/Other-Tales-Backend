package com.othertales.modules.writing.application.port;

import com.othertales.modules.writing.domain.Idea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IdeaRepository {

    Idea save(Idea idea);

    Optional<Idea> findById(UUID id);

    Optional<Idea> findByIdAndProjectId(UUID id, UUID projectId);

    Page<Idea> findAllByProjectId(UUID projectId, Pageable pageable);
}
