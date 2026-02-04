package com.othertales.modules.identity.application.dto;

import com.othertales.modules.identity.domain.ConsentType;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating user consent preferences.
 *
 * @param consentType Type of consent being updated
 * @param granted     Whether consent is granted (true) or revoked (false)
 */
public record UpdateConsentRequest(
        @NotNull(message = "Consent type is required")
        ConsentType consentType,

        @NotNull(message = "Consent value is required")
        Boolean granted
) {}
