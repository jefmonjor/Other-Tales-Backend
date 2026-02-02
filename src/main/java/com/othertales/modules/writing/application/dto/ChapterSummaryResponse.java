package com.othertales.modules.writing.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ChapterSummaryResponse(
        UUID id,
        String title,
        int sortOrder,
        int wordCount,
        Instant updatedAt
) {}
