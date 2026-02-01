package com.othertales.modules.writing.application.dto;

import java.util.List;

public record ProjectListResponse(
        List<ProjectSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
