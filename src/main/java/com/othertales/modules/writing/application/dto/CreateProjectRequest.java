package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank
        @Size(min = 1, max = 255)
        String title,

        @Size(max = 2000)
        String synopsis
) {}
