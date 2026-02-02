package com.othertales.modules.writing.application.dto;

import java.util.List;
import java.util.UUID;

public record ChapterIndexResponse(
        UUID projectId,
        List<ChapterSummaryResponse> chapters,
        int totalChapters,
        int totalWordCount
) {}
