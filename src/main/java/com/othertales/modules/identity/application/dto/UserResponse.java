package com.othertales.modules.identity.application.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        String planType,
        Instant createdAt
) {}
