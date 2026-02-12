package com.othertales.modules.writing.application.dto;

import java.time.Instant;
import java.util.UUID;

public record IdeaResponse(
        UUID id,
        UUID projectId,
        String title,
        String content,
        Instant createdAt,
        Instant updatedAt) {
}
