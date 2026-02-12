package com.othertales.modules.identity.application.usecase;

import com.othertales.modules.identity.application.dto.ProfileResponse;
import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.domain.Profile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentProfileUseCaseTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private GetCurrentProfileUseCase getCurrentProfileUseCase;

    @Test
    void create_should_save_profile_if_not_found() {
        UUID userId = UUID.randomUUID();
        String email = "new@example.com";
        String fullName = "New User";

        when(profileRepository.findById(userId)).thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileResponse response = getCurrentProfileUseCase.execute(userId, email, fullName);

        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.email()).isEqualTo(email);
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void me_should_return_profile_when_found() {
        UUID userId = UUID.randomUUID();
        String email = "existing@example.com";
        String fullName = "Existing User";
        Profile existingProfile = Profile.create(userId, email, fullName);

        when(profileRepository.findById(userId)).thenReturn(Optional.of(existingProfile));

        ProfileResponse response = getCurrentProfileUseCase.execute(userId, email, fullName);

        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.email()).isEqualTo(email);
        // Should NOT save if found
        verify(profileRepository, org.mockito.Mockito.never()).save(any());
    }
}
