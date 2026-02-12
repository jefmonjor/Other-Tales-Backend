package com.othertales.modules.identity.application.usecase;

import com.othertales.common.application.port.AuditLogPort;
import com.othertales.modules.identity.application.dto.ConsentResponse;
import com.othertales.modules.identity.application.dto.UpdateConsentRequest;
import com.othertales.modules.identity.application.port.ConsentLogRepository;
import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.domain.ConsentType;
import com.othertales.modules.identity.domain.Profile;
import com.othertales.modules.identity.domain.ProfileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateConsentUseCaseTest {

    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private ConsentLogRepository consentLogRepository;
    @Mock
    private AuditLogPort auditLogPort;

    private UpdateConsentUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateConsentUseCase(profileRepository, consentLogRepository, auditLogPort);
    }

    @Test
    void execute_should_update_consent_and_log_audit() {
        // Given
        UUID userId = UUID.randomUUID();
        UpdateConsentRequest request = new UpdateConsentRequest(ConsentType.MARKETING_COMMUNICATIONS, true);
        String ip = "127.0.0.1";
        String agent = "TestAgent";

        Profile profile = Profile.create(userId, "test@example.com", "Test User");
        when(profileRepository.findById(userId)).thenReturn(Optional.of(profile));

        // When
        ConsentResponse response = useCase.execute(userId, request, ip, agent);

        // Then
        // 1. Verify Profile update
        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        assertThat(profileCaptor.getValue().isMarketingAccepted()).isTrue();

        // 2. Verify Consent Log
        verify(consentLogRepository).recordConsent(userId, ConsentType.MARKETING_COMMUNICATIONS, true, ip, agent);

        // 3. Verify Audit Log
        verify(auditLogPort).record(
                eq(userId),
                eq("CONSENT.UPDATED"),
                eq(userId.toString()),
                any(),
                eq(ip),
                eq(agent));

        // 4. Verify Response
        assertThat(response).isNotNull();
        assertThat(response.consentType()).isEqualTo(ConsentType.MARKETING_COMMUNICATIONS);
        assertThat(response.granted()).isTrue();
        assertThat(response.recordedAt()).isNotNull();
    }

    @Test
    void execute_should_throw_if_profile_not_found() {
        UUID userId = UUID.randomUUID();
        UpdateConsentRequest request = new UpdateConsentRequest(ConsentType.TERMS_OF_SERVICE, true);

        when(profileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId, request, "ip", "agent"))
                .isInstanceOf(ProfileNotFoundException.class);
    }
}
