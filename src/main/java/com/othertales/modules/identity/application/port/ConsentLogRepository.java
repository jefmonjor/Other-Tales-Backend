package com.othertales.modules.identity.application.port;

import com.othertales.modules.identity.domain.ConsentType;

import java.util.UUID;

public interface ConsentLogRepository {

    void recordConsent(UUID userId, ConsentType consentType, boolean granted, String ipAddress, String userAgent);
}
