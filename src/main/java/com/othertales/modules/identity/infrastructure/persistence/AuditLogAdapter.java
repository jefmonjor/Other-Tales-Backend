package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.common.infrastructure.persistence.AuditLogEntity;
import com.othertales.common.infrastructure.persistence.AuditLogJpaRepository;
import com.othertales.modules.identity.application.port.AuditLogPort;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository
public class AuditLogAdapter implements AuditLogPort {

    private final AuditLogJpaRepository jpaRepository;

    public AuditLogAdapter(AuditLogJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void record(UUID userId, String actionType, String entityId, Map<String, Object> details, String ipAddress, String userAgent) {
        var entity = AuditLogEntity.create(userId, actionType, entityId, details, ipAddress, userAgent);
        jpaRepository.save(entity);
    }
}
