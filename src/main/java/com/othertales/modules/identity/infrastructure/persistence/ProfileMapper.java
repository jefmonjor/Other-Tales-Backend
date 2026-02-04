package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.domain.PlanType;
import com.othertales.modules.identity.domain.Profile;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class ProfileMapper {

    // De DOMINIO (Instant) a ENTIDAD (OffsetDateTime)
    public ProfileEntity toEntity(Profile profile) {
        var entity = new ProfileEntity();
        entity.setId(profile.getId());
        entity.setEmail(profile.getEmail());
        entity.setFullName(profile.getFullName());
        entity.setPlanType(toEntityPlanType(profile.getPlanType()));

        // FIX: Convertir Instant -> OffsetDateTime
        if (profile.getCreatedAt() != null) {
            entity.setCreatedAt(profile.getCreatedAt().atOffset(ZoneOffset.UTC));
        }
        if (profile.getUpdatedAt() != null) {
            entity.setUpdatedAt(profile.getUpdatedAt().atOffset(ZoneOffset.UTC));
        }

        entity.setVersion(profile.getVersion());
        return entity;
    }

    // De ENTIDAD (OffsetDateTime) a DOMINIO (Instant)
    public Profile toDomain(ProfileEntity entity) {
        return Profile.reconstitute(
                entity.getId(),
                entity.getEmail(),
                entity.getFullName(),
                toDomainPlanType(entity.getPlanType()),
                // FIX: Convertir OffsetDateTime -> Instant
                entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant() : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().toInstant() : null,
                entity.getVersion()
        );
    }

    private ProfileEntity.PlanTypeEntity toEntityPlanType(PlanType planType) {
        return switch (planType) {
            case FREE -> ProfileEntity.PlanTypeEntity.FREE;
            case PRO -> ProfileEntity.PlanTypeEntity.PRO;
        };
    }

    private PlanType toDomainPlanType(ProfileEntity.PlanTypeEntity planType) {
        return switch (planType) {
            case FREE -> PlanType.FREE;
            case PRO -> PlanType.PRO;
        };
    }
}
