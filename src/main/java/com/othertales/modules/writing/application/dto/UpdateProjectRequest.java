package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
        @Size(min = 1, max = 255, message = "{project.title.size}")
        String title,

        @Size(max = 2000, message = "{project.synopsis.size}")
        String synopsis,

        @Size(max = 100, message = "{project.genre.size}")
        String genre,

        @Min(value = 1, message = "{project.targetWordCount.min}")
        Integer targetWordCount,

        @Size(max = 500, message = "Cover URL must not exceed 500 characters")
        String coverUrl,

        String status
) {}
