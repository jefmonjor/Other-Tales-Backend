package com.othertales.modules.identity.domain;

import com.othertales.common.domain.ErrorCodes;
import com.othertales.common.domain.ResourceNotFoundException;

import java.util.UUID;

public class ProfileNotFoundException extends ResourceNotFoundException {

    public ProfileNotFoundException(UUID profileId) {
        super("Profile not found with id: " + profileId, ErrorCodes.PROFILE_NOT_FOUND);
    }
}
