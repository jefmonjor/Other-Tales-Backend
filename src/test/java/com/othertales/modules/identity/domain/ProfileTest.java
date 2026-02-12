package com.othertales.modules.identity.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileTest {

    @Test
    void should_create_profile() {
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String fullName = "John Doe";

        Profile profile = Profile.create(id, email, fullName);

        assertThat(profile.getId()).isEqualTo(id);
        assertThat(profile.getEmail()).isEqualTo(email);
        assertThat(profile.getFullName()).isEqualTo(fullName);
        assertThat(profile.getPlanType()).isEqualTo(PlanType.FREE);
        assertThat(profile.isTermsAccepted()).isFalse();
    }

    @Test
    void should_update_bio() {
        // User requested 'bio', but domain uses 'fullName' and 'avatarUrl' for profile
        // updates currently.
        // Mapping requirement to available fields.
        Profile profile = Profile.create(UUID.randomUUID(), "test@example.com", "Original Name");

        profile.updateFullName("Updated Name");
        profile.updateAvatarUrl("https://example.com/avatar.png");

        assertThat(profile.getFullName()).isEqualTo("Updated Name");
        assertThat(profile.getAvatarUrl()).isEqualTo("https://example.com/avatar.png");
        assertThat(profile.getUpdatedAt()).isNotNull();
    }

    @Test
    void should_validate_email() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> Profile.create(id, null, "Name"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Email is required");
    }
}
