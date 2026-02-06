package com.othertales.modules.identity.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String email,
        String fullName,
        String avatarUrl,
        String planType,
        boolean termsAccepted,
        boolean privacyAccepted,
        boolean marketingAccepted,
        Instant createdAt
) {}
