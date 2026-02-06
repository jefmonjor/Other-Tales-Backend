package com.othertales.modules.identity.application.usecase;

import com.othertales.modules.identity.application.dto.ConsentResponse;
import com.othertales.modules.identity.application.dto.UpdateConsentRequest;
import com.othertales.modules.identity.application.port.AuditLogPort;
import com.othertales.modules.identity.application.port.ConsentLogRepository;
import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.domain.ProfileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * AUDIT FIX #7 (FASE 2.2): Fully decoupled from infrastructure.
 * Uses ProfileRepository port + ConsentLogRepository port + AuditLogPort.
 * No JPA entity or repository imports.
 *
 * AUDIT FIX #10 (FASE 3.1): getPreviousConsentValue now reads the real
 * value from the Profile domain object BEFORE mutation.
 *
 * AUDIT FIX #12 (FASE 3.3): All timestamps standardized to Instant.
 */
@Service
public class UpdateConsentUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateConsentUseCase.class);
    private static final String ACTION_CONSENT_UPDATED = "CONSENT.UPDATED";

    private final ProfileRepository profileRepository;
    private final ConsentLogRepository consentLogRepository;
    private final AuditLogPort auditLogPort;

    public UpdateConsentUseCase(
            ProfileRepository profileRepository,
            ConsentLogRepository consentLogRepository,
            AuditLogPort auditLogPort
    ) {
        this.profileRepository = profileRepository;
        this.consentLogRepository = consentLogRepository;
        this.auditLogPort = auditLogPort;
    }

    @Transactional
    public ConsentResponse execute(
            UUID userId,
            UpdateConsentRequest request,
            String ipAddress,
            String userAgent
    ) {
        log.info("Updating consent for user {} - type: {}, granted: {}",
                userId, request.consentType(), request.granted());

        var profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        // Capture previous value BEFORE mutation
        var previousValue = profile.getConsentValue(request.consentType());

        // Mutate consent on the domain object
        profile.updateConsent(request.consentType(), request.granted());
        profileRepository.save(profile);

        var now = Instant.now();

        // Record immutable consent log
        consentLogRepository.recordConsent(
                userId, request.consentType(), request.granted(), ipAddress, userAgent);

        // Record audit log
        auditLogPort.record(
                userId,
                ACTION_CONSENT_UPDATED,
                userId.toString(),
                Map.of(
                        "consentType", request.consentType().name(),
                        "granted", request.granted(),
                        "previousValue", previousValue
                ),
                ipAddress,
                userAgent
        );

        log.info("Consent updated successfully for user {} - type: {}", userId, request.consentType());

        return new ConsentResponse(request.consentType(), request.granted(), now);
    }

}
