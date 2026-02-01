package com.othertales.modules.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class User {

    private UUID id;
    private String email;
    private String passwordHash;
    private String fullName;
    private PlanType planType;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    private User() {}

    public static User create(String email, String passwordHash, String fullName) {
        var user = new User();
        user.id = UUID.randomUUID();
        user.email = Objects.requireNonNull(email, "Email is required");
        user.passwordHash = Objects.requireNonNull(passwordHash, "Password hash is required");
        user.fullName = fullName;
        user.planType = PlanType.FREE;
        user.createdAt = Instant.now();
        user.updatedAt = user.createdAt;
        user.version = 0L;
        return user;
    }

    public static User reconstitute(
            UUID id,
            String email,
            String passwordHash,
            String fullName,
            PlanType planType,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        var user = new User();
        user.id = id;
        user.email = email;
        user.passwordHash = passwordHash;
        user.fullName = fullName;
        user.planType = planType;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        user.version = version;
        return user;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
