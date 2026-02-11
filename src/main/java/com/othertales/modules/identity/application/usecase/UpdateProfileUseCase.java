package com.othertales.modules.identity.application.usecase;

import com.othertales.modules.identity.application.dto.ProfileResponse;
import com.othertales.modules.identity.application.dto.UpdateProfileRequest;
import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.domain.ProfileNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * AUDIT FIX #6: Use case for updating the current user's profile (fullName, avatarUrl).
 * Only non-null fields in the request are applied.
 */
public class UpdateProfileUseCase {

    private final ProfileRepository profileRepository;

    public UpdateProfileUseCase(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional
    public ProfileResponse execute(UUID userId, UpdateProfileRequest request) {
        var profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        if (request.fullName() != null) {
            profile.updateFullName(request.fullName());
        }
        if (request.avatarUrl() != null) {
            profile.updateAvatarUrl(request.avatarUrl());
        }

        var saved = profileRepository.save(profile);

        return new ProfileResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getFullName(),
                saved.getAvatarUrl(),
                saved.getPlanType().name(),
                saved.isTermsAccepted(),
                saved.getTermsAcceptedAt(),
                saved.isPrivacyAccepted(),
                saved.getPrivacyAcceptedAt(),
                saved.isMarketingAccepted(),
                saved.getMarketingAcceptedAt(),
                saved.getCreatedAt()
        );
    }
}
