package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.Size;

public record UpdateIdeaRequest(
        @Size(max = 255, message = "Title must be less than 255 characters") String title,

        String content) {
}
