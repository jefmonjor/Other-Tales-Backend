package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCharacterRequest(
        @NotBlank(message = "Name is required") @Size(max = 255, message = "Name must be less than 255 characters") String name,

        String role,
        String description,
        String physicalDescription,
        String imageUrl) {
}
