package com.othertales.modules.identity.application.dto;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating user profile fields.
 * Both fields are optional; only non-null fields will be updated.
 *
 * @param fullName  Optional new display name (2-100 chars)
 * @param avatarUrl Optional new avatar URL (max 500 chars)
 */
public record UpdateProfileRequest(
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        String fullName,

        @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
        String avatarUrl
) {}
