package com.othertales.modules.identity.application.usecase;

import com.othertales.modules.identity.application.dto.ProfileResponse;
import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.domain.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * AUDIT FIX #5: Auto-provisions a profile on first login if one does not exist.
 * Extracts user info from JWT claims (email, full_name) to create the profile.
 */
public class GetCurrentProfileUseCase {

    private final ProfileRepository profileRepository;

    public GetCurrentProfileUseCase(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional
    public ProfileResponse execute(UUID userId, String email, String fullName) {
        var profile = profileRepository.findById(userId)
                .orElseGet(() -> {
                    var newProfile = Profile.create(userId, email, fullName);
                    return profileRepository.save(newProfile);
                });

        return new ProfileResponse(
                profile.getId(),
                profile.getEmail(),
                profile.getFullName(),
                profile.getAvatarUrl(),
                profile.getPlanType().name(),
                profile.isTermsAccepted(),
                profile.getTermsAcceptedAt(),
                profile.isPrivacyAccepted(),
                profile.getPrivacyAcceptedAt(),
                profile.isMarketingAccepted(),
                profile.getMarketingAcceptedAt(),
                profile.getCreatedAt()
        );
    }
}
