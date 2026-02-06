package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.domain.PlanType;
import com.othertales.modules.identity.domain.Profile;
import org.springframework.stereotype.Component;

/**
 * AUDIT FIX #3 (FASE 1.3): toEntity now accepts existingEntity parameter
 * to preserve isNew=false state for updates.
 * AUDIT FIX #12 (FASE 3.3): Removed OffsetDateTime conversions - all Instant now.
 */
@Component
public class ProfileMapper {

    public ProfileEntity toEntity(Profile profile) {
        var entity = new ProfileEntity();
        applyToEntity(entity, profile);
        return entity;
    }

    public ProfileEntity toEntity(Profile profile, ProfileEntity existingEntity) {
        applyToEntity(existingEntity, profile);
        return existingEntity;
    }

    private void applyToEntity(ProfileEntity entity, Profile profile) {
        entity.setId(profile.getId());
        entity.setEmail(profile.getEmail());
        entity.setFullName(profile.getFullName());
        entity.setAvatarUrl(profile.getAvatarUrl());
        entity.setPlanType(toEntityPlanType(profile.getPlanType()));
        entity.setTermsAccepted(profile.isTermsAccepted());
        entity.setTermsAcceptedAt(profile.getTermsAcceptedAt());
        entity.setPrivacyAccepted(profile.isPrivacyAccepted());
        entity.setPrivacyAcceptedAt(profile.getPrivacyAcceptedAt());
        entity.setMarketingAccepted(profile.isMarketingAccepted());
        entity.setMarketingAcceptedAt(profile.getMarketingAcceptedAt());
        entity.setCreatedAt(profile.getCreatedAt());
        entity.setUpdatedAt(profile.getUpdatedAt());
        entity.setVersion(profile.getVersion());
    }

    public Profile toDomain(ProfileEntity entity) {
        return Profile.reconstitute(
                entity.getId(),
                entity.getEmail(),
                entity.getFullName(),
                entity.getAvatarUrl(),
                toDomainPlanType(entity.getPlanType()),
                entity.isTermsAccepted(),
                entity.getTermsAcceptedAt(),
                entity.isPrivacyAccepted(),
                entity.getPrivacyAcceptedAt(),
                entity.isMarketingAccepted(),
                entity.getMarketingAcceptedAt(),
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
