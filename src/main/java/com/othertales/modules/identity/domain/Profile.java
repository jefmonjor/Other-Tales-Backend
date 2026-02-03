package com.othertales.modules.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Profile {

    private UUID id;
    private String email;
    private String fullName;
    private PlanType planType;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    private Profile() {}

    public static Profile create(UUID id, String email, String fullName) {
        var profile = new Profile();
        profile.id = Objects.requireNonNull(id, "ID is required (from Supabase auth)");
        profile.email = Objects.requireNonNull(email, "Email is required");
        profile.fullName = fullName;
        profile.planType = PlanType.FREE;
        profile.createdAt = Instant.now();
        profile.updatedAt = profile.createdAt;
        profile.version = 0L;
        return profile;
    }

    public static Profile reconstitute(
            UUID id,
            String email,
            String fullName,
            PlanType planType,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        var profile = new Profile();
        profile.id = id;
        profile.email = email;
        profile.fullName = fullName;
        profile.planType = planType;
        profile.createdAt = createdAt;
        profile.updatedAt = updatedAt;
        profile.version = version;
        return profile;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void upgradeToPro() {
        this.planType = PlanType.PRO;
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(id, profile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
