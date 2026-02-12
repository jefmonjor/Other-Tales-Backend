package com.othertales.modules.writing.application.dto;

import java.time.Instant;
import java.util.UUID;

public record StoryResponse(
        UUID id,
        UUID projectId,
        String title,
        String synopsis,
        String theme,
        String secondaryPlots,
        String others,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt) {
}
