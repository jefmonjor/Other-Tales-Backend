package com.othertales.modules.writing.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ChapterResponse(
        UUID id,
        UUID projectId,
        String title,
        String content,
        int sortOrder,
        int wordCount,
        String status,
        Instant createdAt,
        Instant updatedAt
) {}
