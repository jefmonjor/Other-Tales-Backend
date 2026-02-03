package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.domain.PlanType;
import com.othertales.modules.identity.domain.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileEntity toEntity(Profile profile) {
        var entity = new ProfileEntity();
        entity.setId(profile.getId());
        entity.setEmail(profile.getEmail());
        entity.setFullName(profile.getFullName());
        entity.setPlanType(toEntityPlanType(profile.getPlanType()));
        entity.setCreatedAt(profile.getCreatedAt());
        entity.setUpdatedAt(profile.getUpdatedAt());
        entity.setVersion(profile.getVersion());
        return entity;
    }

    public Profile toDomain(ProfileEntity entity) {
        return Profile.reconstitute(
                entity.getId(),
                entity.getEmail(),
                entity.getFullName(),
                toDomainPlanType(entity.getPlanType()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
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
