package com.othertales.modules.writing.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ProjectSummaryResponse(
        UUID id,
        String title,
        String status,
        Instant updatedAt
) {}
