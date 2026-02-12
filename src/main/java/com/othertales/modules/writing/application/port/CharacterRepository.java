package com.othertales.modules.writing.application.port;

import com.othertales.modules.writing.domain.Character;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CharacterRepository {

    Character save(Character character);

    Optional<Character> findById(UUID id);

    Optional<Character> findByIdAndProjectId(UUID id, UUID projectId);

    Page<Character> findAllByProjectId(UUID projectId, Pageable pageable);
}
