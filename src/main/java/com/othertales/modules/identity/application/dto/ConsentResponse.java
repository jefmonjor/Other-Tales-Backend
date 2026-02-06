package com.othertales.modules.identity.application.dto;

import com.othertales.modules.identity.domain.ConsentType;

import java.time.Instant;

/**
 * Response DTO for consent update confirmation.
 *
 * @param consentType Type of consent that was updated
 * @param granted     Current consent status
 * @param recordedAt  When the consent change was recorded
 */
public record ConsentResponse(
        ConsentType consentType,
        boolean granted,
        Instant recordedAt
) {}
