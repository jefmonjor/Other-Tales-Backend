package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.application.port.ConsentLogRepository;
import com.othertales.modules.identity.domain.ConsentType;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ConsentLogJpaAdapter implements ConsentLogRepository {

    private final ConsentLogJpaRepository jpaRepository;

    public ConsentLogJpaAdapter(ConsentLogJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void recordConsent(UUID userId, ConsentType consentType, boolean granted, String ipAddress, String userAgent) {
        var entity = ConsentLogEntity.create(userId, consentType, granted, ipAddress, userAgent);
        jpaRepository.save(entity);
    }
}
