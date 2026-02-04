package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.domain.ConsentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for consent audit logs.
 */
@Repository
public interface ConsentLogJpaRepository extends JpaRepository<ConsentLogEntity, UUID> {

    List<ConsentLogEntity> findByUserIdOrderByRecordedAtDesc(UUID userId);

    List<ConsentLogEntity> findByUserIdAndConsentTypeOrderByRecordedAtDesc(UUID userId, ConsentType consentType);
}
