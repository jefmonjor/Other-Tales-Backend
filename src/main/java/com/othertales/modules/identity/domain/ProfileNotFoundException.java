package com.othertales.modules.identity.domain;

import java.util.UUID;

public class ProfileNotFoundException extends RuntimeException {

    private final UUID profileId;

    public ProfileNotFoundException(UUID profileId) {
        super("Profile not found with id: " + profileId);
        this.profileId = profileId;
    }

    public UUID getProfileId() {
        return profileId;
    }
}
