package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReorderChapterRequest(
        @NotNull(message = "{chapter.sortOrder.required}")
        @Min(value = 0, message = "{chapter.sortOrder.min}")
        Integer newSortOrder
) {}
