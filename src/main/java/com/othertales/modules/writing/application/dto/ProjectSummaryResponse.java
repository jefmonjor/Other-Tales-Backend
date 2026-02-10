package com.othertales.modules.writing.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ProjectSummaryResponse(
        UUID id,
        String title,
        String genre,
        int currentWordCount,
        int targetWordCount,
        String coverUrl,
        String status,
        Instant updatedAt
) {}
