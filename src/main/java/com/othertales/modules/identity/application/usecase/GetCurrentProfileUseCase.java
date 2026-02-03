package com.othertales.modules.identity.application.usecase;

import com.othertales.modules.identity.application.dto.ProfileResponse;
import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.domain.ProfileNotFoundException;

import java.util.UUID;

public class GetCurrentProfileUseCase {

    private final ProfileRepository profileRepository;

    public GetCurrentProfileUseCase(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileResponse execute(UUID userId) {
        var profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        return new ProfileResponse(
                profile.getId(),
                profile.getEmail(),
                profile.getFullName(),
                profile.getPlanType().name(),
                profile.getCreatedAt()
        );
    }
}
