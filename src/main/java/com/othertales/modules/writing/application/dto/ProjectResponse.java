package com.othertales.modules.writing.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String title,
        String synopsis,
        String coverUrl,
        String status,
        Instant createdAt,
        Instant updatedAt
) {}
