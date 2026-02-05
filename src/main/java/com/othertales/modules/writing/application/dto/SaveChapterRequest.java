package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SaveChapterRequest(
        @NotNull(message = "{chapter.projectId.required}")
        UUID projectId,

        UUID id,

        @Size(min = 1, max = 255, message = "{chapter.title.size}")
        String title,

        String content,

        Integer orderIndex
) {
    public SaveChapterRequest {
        if (title == null || title.isBlank()) {
            title = "Untitled Chapter";
        }
        if (content == null) {
            content = "";
        }
    }
}
