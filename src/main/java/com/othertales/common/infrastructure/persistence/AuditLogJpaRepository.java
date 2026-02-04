package com.othertales.common.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for audit logs.
 */
@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, UUID> {

    List<AuditLogEntity> findByUserIdOrderByPerformedAtDesc(UUID userId);

    List<AuditLogEntity> findByActionTypeOrderByPerformedAtDesc(String actionType);

    List<AuditLogEntity> findByEntityIdOrderByPerformedAtDesc(String entityId);
}
