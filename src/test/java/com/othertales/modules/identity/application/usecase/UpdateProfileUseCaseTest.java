package com.othertales.modules.identity.application.usecase;

import com.othertales.modules.identity.application.dto.ProfileResponse;
import com.othertales.modules.identity.application.dto.UpdateProfileRequest;
import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.domain.Profile;
import com.othertales.modules.identity.domain.ProfileNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProfileUseCaseTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private UpdateProfileUseCase updateProfileUseCase;

    @Test
    void update_should_modify_profile() {
        UUID userId = UUID.randomUUID();
        Profile existingProfile = Profile.create(userId, "test@example.com", "Old Name");
        UpdateProfileRequest request = new UpdateProfileRequest("New Name", "http://avatar.com");

        when(profileRepository.findById(userId)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileResponse response = updateProfileUseCase.execute(userId, request);

        assertThat(response.fullName()).isEqualTo("New Name");
        assertThat(response.avatarUrl()).isEqualTo("http://avatar.com");
        verify(profileRepository).save(existingProfile);
    }

    @Test
    void should_throw_when_not_found() {
        UUID userId = UUID.randomUUID();
        UpdateProfileRequest request = new UpdateProfileRequest("Name", null);

        when(profileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProfileUseCase.execute(userId, request))
                .isInstanceOf(ProfileNotFoundException.class);
    }
}
