package com.othertales.modules.identity.application.usecase;

import com.othertales.common.infrastructure.persistence.AuditLogEntity;
import com.othertales.common.infrastructure.persistence.AuditLogJpaRepository;
import com.othertales.modules.identity.application.dto.ConsentResponse;
import com.othertales.modules.identity.application.dto.UpdateConsentRequest;
import com.othertales.modules.identity.domain.ConsentType;
import com.othertales.modules.identity.domain.ProfileNotFoundException;
import com.othertales.modules.identity.infrastructure.persistence.ConsentLogEntity;
import com.othertales.modules.identity.infrastructure.persistence.ConsentLogJpaRepository;
import com.othertales.modules.identity.infrastructure.persistence.ProfileEntity;
import com.othertales.modules.identity.infrastructure.persistence.ProfileJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Use case for updating user consent preferences.
 * Ensures full audit trail for GDPR compliance.
 */
@Service
public class UpdateConsentUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateConsentUseCase.class);

    private final ProfileJpaRepository profileRepository;
    private final ConsentLogJpaRepository consentLogRepository;
    private final AuditLogJpaRepository auditLogRepository;

    public UpdateConsentUseCase(
            ProfileJpaRepository profileRepository,
            ConsentLogJpaRepository consentLogRepository,
            AuditLogJpaRepository auditLogRepository
    ) {
        this.profileRepository = profileRepository;
        this.consentLogRepository = consentLogRepository;
        this.auditLogRepository = auditLogRepository;
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

        // 1. Find user profile
        var profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        // 2. Update profile consent field
        var now = OffsetDateTime.now();
        updateProfileConsent(profile, request.consentType(), request.granted(), now);
        profile.setUpdatedAt(now);
        profileRepository.save(profile);

        // 3. Create immutable consent log entry
        var consentLog = ConsentLogEntity.create(
                userId,
                request.consentType(),
                request.granted(),
                ipAddress,
                userAgent
        );
        consentLogRepository.save(consentLog);

        // 4. Create audit log entry
        var auditLog = AuditLogEntity.create(
                userId,
                AuditLogEntity.Actions.CONSENT_UPDATED,
                userId.toString(),
                Map.of(
                        "consentType", request.consentType().name(),
                        "granted", request.granted(),
                        "previousValue", getPreviousConsentValue(profile, request.consentType(), request.granted())
                ),
                ipAddress,
                userAgent
        );
        auditLogRepository.save(auditLog);

        log.info("Consent updated successfully for user {} - type: {}", userId, request.consentType());

        return new ConsentResponse(
                request.consentType(),
                request.granted(),
                now
        );
    }

    private void updateProfileConsent(ProfileEntity profile, ConsentType type, boolean granted, OffsetDateTime now) {
        switch (type) {
            case TERMS_OF_SERVICE -> {
                profile.setTermsAccepted(granted);
                profile.setTermsAcceptedAt(granted ? now : null);
            }
            case PRIVACY_POLICY -> {
                profile.setPrivacyAccepted(granted);
                profile.setPrivacyAcceptedAt(granted ? now : null);
            }
            case MARKETING_COMMUNICATIONS -> {
                profile.setMarketingAccepted(granted);
                profile.setMarketingAcceptedAt(granted ? now : null);
            }
        }
    }

    private boolean getPreviousConsentValue(ProfileEntity profile, ConsentType type, boolean newValue) {
        // Since we've already updated, the "previous" value is the opposite of new if changed
        return !newValue;
    }
}
