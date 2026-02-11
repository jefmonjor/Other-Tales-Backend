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
        Instant termsAcceptedAt,
        boolean privacyAccepted,
        Instant privacyAcceptedAt,
        boolean marketingAccepted,
        Instant marketingAcceptedAt,
        Instant createdAt
) {}
