package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChapterRequest(
        @NotBlank(message = "{chapter.title.required}")
        @Size(min = 1, max = 255, message = "{chapter.title.size}")
        String title,

        String content,

        Integer sortOrder
) {
    public CreateChapterRequest {
        if (content == null) {
            content = "";
        }
    }
}
