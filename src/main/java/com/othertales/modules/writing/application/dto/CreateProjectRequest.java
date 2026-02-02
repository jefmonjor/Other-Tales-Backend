package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank(message = "{project.title.required}")
        @Size(min = 1, max = 255, message = "{project.title.size}")
        String title,

        @Size(max = 2000, message = "{project.synopsis.size}")
        String synopsis,

        @Size(max = 100, message = "{project.genre.size}")
        String genre,

        @Min(value = 0, message = "{project.targetWordCount.min}")
        Integer targetWordCount
) {
    public CreateProjectRequest {
        if (targetWordCount == null) {
            targetWordCount = 50000;
        }
    }
}
