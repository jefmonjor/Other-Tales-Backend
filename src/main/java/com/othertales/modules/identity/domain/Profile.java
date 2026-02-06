package com.othertales.modules.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Profile {

    private UUID id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private PlanType planType;
    private boolean termsAccepted;
    private Instant termsAcceptedAt;
    private boolean privacyAccepted;
    private Instant privacyAcceptedAt;
    private boolean marketingAccepted;
    private Instant marketingAcceptedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    private Profile() {}

    public static Profile create(UUID id, String email, String fullName) {
        var profile = new Profile();
        profile.id = Objects.requireNonNull(id, "ID is required (from Supabase auth)");
        profile.email = Objects.requireNonNull(email, "Email is required");
        profile.fullName = fullName;
        profile.avatarUrl = null;
        profile.planType = PlanType.FREE;
        profile.termsAccepted = false;
        profile.privacyAccepted = false;
        profile.marketingAccepted = false;
        profile.createdAt = Instant.now();
        profile.updatedAt = profile.createdAt;
        profile.version = 0L;
        return profile;
    }

    public static Profile reconstitute(
            UUID id,
            String email,
            String fullName,
            String avatarUrl,
            PlanType planType,
            boolean termsAccepted,
            Instant termsAcceptedAt,
            boolean privacyAccepted,
            Instant privacyAcceptedAt,
            boolean marketingAccepted,
            Instant marketingAcceptedAt,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        var profile = new Profile();
        profile.id = id;
        profile.email = email;
        profile.fullName = fullName;
        profile.avatarUrl = avatarUrl;
        profile.planType = planType;
        profile.termsAccepted = termsAccepted;
        profile.termsAcceptedAt = termsAcceptedAt;
        profile.privacyAccepted = privacyAccepted;
        profile.privacyAcceptedAt = privacyAcceptedAt;
        profile.marketingAccepted = marketingAccepted;
        profile.marketingAcceptedAt = marketingAcceptedAt;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public Instant getTermsAcceptedAt() {
        return termsAcceptedAt;
    }

    public boolean isPrivacyAccepted() {
        return privacyAccepted;
    }

    public Instant getPrivacyAcceptedAt() {
        return privacyAcceptedAt;
    }

    public boolean isMarketingAccepted() {
        return marketingAccepted;
    }

    public Instant getMarketingAcceptedAt() {
        return marketingAcceptedAt;
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

    public void updateConsent(ConsentType consentType, boolean granted) {
        var now = Instant.now();
        switch (consentType) {
            case TERMS_OF_SERVICE -> {
                this.termsAccepted = granted;
                this.termsAcceptedAt = now;
            }
            case PRIVACY_POLICY -> {
                this.privacyAccepted = granted;
                this.privacyAcceptedAt = now;
            }
            case MARKETING_COMMUNICATIONS -> {
                this.marketingAccepted = granted;
                this.marketingAcceptedAt = now;
            }
        }
        this.updatedAt = now;
    }

    public boolean getConsentValue(ConsentType consentType) {
        return switch (consentType) {
            case TERMS_OF_SERVICE -> termsAccepted;
            case PRIVACY_POLICY -> privacyAccepted;
            case MARKETING_COMMUNICATIONS -> marketingAccepted;
        };
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
