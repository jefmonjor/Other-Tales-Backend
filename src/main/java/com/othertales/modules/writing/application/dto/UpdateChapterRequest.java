package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.Size;

public record UpdateChapterRequest(
        @Size(min = 1, max = 255, message = "{chapter.title.size}")
        String title,

        String content,

        String status
) {}
