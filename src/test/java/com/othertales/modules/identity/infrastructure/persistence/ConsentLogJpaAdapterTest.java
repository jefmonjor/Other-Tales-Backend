package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.domain.ConsentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsentLogJpaAdapterTest {

    @Mock
    private ConsentLogJpaRepository jpaRepository;

    private ConsentLogJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ConsentLogJpaAdapter(jpaRepository);
    }

    @Test
    void should_record_consent() {
        // Given
        UUID userId = UUID.randomUUID();
        ConsentType type = ConsentType.PRIVACY_POLICY;
        String ip = "192.168.1.1";
        String agent = "TestAgent";

        // When
        adapter.recordConsent(userId, type, true, ip, agent);

        // Then
        ArgumentCaptor<ConsentLogEntity> captor = ArgumentCaptor.forClass(ConsentLogEntity.class);
        verify(jpaRepository).save(captor.capture());

        ConsentLogEntity captured = captor.getValue();
        assertThat(captured.getUserId()).isEqualTo(userId);
        assertThat(captured.getConsentType()).isEqualTo(type);
        assertThat(captured.isConsentGiven()).isTrue();
        assertThat(captured.getIpAddress()).isEqualTo(ip);
        assertThat(captured.getUserAgent()).isEqualTo(agent);
        assertThat(captured.getRecordedAt()).isNotNull();
    }
}
