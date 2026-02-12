package com.othertales.modules.writing.application.dto;

import java.time.Instant;
import java.util.UUID;

public record CharacterResponse(
        UUID id,
        UUID projectId,
        String name,
        String role,
        String description,
        String physicalDescription,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt) {
}
