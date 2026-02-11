package com.othertales.modules.identity.infrastructure.web;

import com.othertales.modules.identity.application.dto.ConsentResponse;
import com.othertales.modules.identity.application.dto.UpdateConsentRequest;
import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.application.usecase.UpdateConsentUseCase;
import com.othertales.modules.identity.domain.ConsentType;
import com.othertales.modules.identity.domain.ProfileNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing user consent preferences.
 * Provides endpoints for GDPR-compliant consent management.
 */
@RestController
@RequestMapping("/api/v1/user")
public class ConsentController {

    private static final Logger log = LoggerFactory.getLogger(ConsentController.class);

    private final UpdateConsentUseCase updateConsentUseCase;
    private final ProfileRepository profileRepository;

    public ConsentController(UpdateConsentUseCase updateConsentUseCase, ProfileRepository profileRepository) {
        this.updateConsentUseCase = updateConsentUseCase;
        this.profileRepository = profileRepository;
    }

    @GetMapping("/consent")
    public ResponseEntity<List<ConsentResponse>> getCurrentConsent(@AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        var profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        var consents = Arrays.stream(ConsentType.values())
                .map(type -> new ConsentResponse(
                        type,
                        profile.getConsentValue(type),
                        switch (type) {
                            case TERMS_OF_SERVICE -> profile.getTermsAcceptedAt();
                            case PRIVACY_POLICY -> profile.getPrivacyAcceptedAt();
                            case MARKETING_COMMUNICATIONS -> profile.getMarketingAcceptedAt();
                        }
                ))
                .toList();

        return ResponseEntity.ok(consents);
    }

    @PostMapping("/consent")
    public ResponseEntity<ConsentResponse> updateConsent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateConsentRequest request,
            HttpServletRequest httpRequest
    ) {
        var userId = UUID.fromString(jwt.getSubject());
        var ipAddress = extractClientIp(httpRequest);
        var userAgent = httpRequest.getHeader("User-Agent");

        log.info("Consent update request from user {} for type {}", userId, request.consentType());

        var response = updateConsentUseCase.execute(userId, request, ipAddress, userAgent);

        return ResponseEntity.ok(response);
    }

    /**
     * Extract client IP address, handling proxies and load balancers.
     */
    private String extractClientIp(HttpServletRequest request) {
        var xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // Take the first IP in the chain (original client)
            return xForwardedFor.split(",")[0].trim();
        }
        var xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
