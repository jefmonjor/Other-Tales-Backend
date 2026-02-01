package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank
        @Size(min = 1, max = 255)
        String title,

        @Size(max = 2000)
        String synopsis,

        @Size(max = 100)
        String genre,

        @Min(0)
        Integer targetWordCount
) {
    public CreateProjectRequest {
        if (targetWordCount == null) {
            targetWordCount = 50000;
        }
    }
}
