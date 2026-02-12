package com.othertales.modules.identity.infrastructure.persistence;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileEntityTest {

    @Test
    void should_set_and_get_values() {
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String fullName = "Test User";
        String avatar = "http://avatar.url";
        Instant now = Instant.now();

        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail(email);
        entity.setFullName(fullName);
        entity.setAvatarUrl(avatar);
        entity.setPlanType(ProfileEntity.PlanTypeEntity.PRO);
        entity.setTermsAccepted(true);
        entity.setTermsAcceptedAt(now);
        entity.setPrivacyAccepted(true);
        entity.setPrivacyAcceptedAt(now);
        entity.setMarketingAccepted(true);
        entity.setMarketingAcceptedAt(now);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setVersion(1L);

        // Assert Getters
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo(email);
        assertThat(entity.getFullName()).isEqualTo(fullName);
        assertThat(entity.getAvatarUrl()).isEqualTo(avatar);
        assertThat(entity.getPlanType()).isEqualTo(ProfileEntity.PlanTypeEntity.PRO);
        assertThat(entity.isTermsAccepted()).isTrue();
        assertThat(entity.getTermsAcceptedAt()).isEqualTo(now);
        assertThat(entity.isPrivacyAccepted()).isTrue();
        assertThat(entity.getPrivacyAcceptedAt()).isEqualTo(now);
        assertThat(entity.isMarketingAccepted()).isTrue();
        assertThat(entity.getMarketingAcceptedAt()).isEqualTo(now);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
        assertThat(entity.getVersion()).isEqualTo(1L);
    }

    @Test
    void should_handle_lifecycle_methods() {
        ProfileEntity entity = new ProfileEntity();
        assertThat(entity.isNew()).isTrue();

        entity.markNotNew();
        assertThat(entity.isNew()).isFalse();
    }

    @Test
    void should_verify_enum_values() {
        assertThat(ProfileEntity.PlanTypeEntity.values()).hasSize(2);
        assertThat(ProfileEntity.PlanTypeEntity.valueOf("FREE")).isEqualTo(ProfileEntity.PlanTypeEntity.FREE);
    }
}
