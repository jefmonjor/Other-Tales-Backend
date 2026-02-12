package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.Size;

public record UpdateStoryRequest(
        @Size(max = 255, message = "Title must be less than 255 characters") String title,

        String synopsis,
        String theme,
        String secondaryPlots,
        String others,
        String imageUrl) {
}
