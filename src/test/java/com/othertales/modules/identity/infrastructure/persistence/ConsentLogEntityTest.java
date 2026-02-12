package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.domain.ConsentType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsentLogEntityTest {

    @Test
    void should_create_and_access_fields() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String ip = "127.0.0.1";
        String agent = "Mozilla/5.0";
        Instant now = Instant.now();

        ConsentLogEntity entity = new ConsentLogEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setConsentType(ConsentType.MARKETING_COMMUNICATIONS);
        entity.setConsentGiven(true);
        entity.setIpAddress(ip);
        entity.setUserAgent(agent);
        entity.setRecordedAt(now);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getConsentType()).isEqualTo(ConsentType.MARKETING_COMMUNICATIONS);
        assertThat(entity.isConsentGiven()).isTrue();
        assertThat(entity.getIpAddress()).isEqualTo(ip);
        assertThat(entity.getUserAgent()).isEqualTo(agent);
        assertThat(entity.getRecordedAt()).isEqualTo(now);
    }

    @Test
    void should_set_recorded_at_on_pre_persist() {
        ConsentLogEntity entity = new ConsentLogEntity();
        assertThat(entity.getRecordedAt()).isNull();

        entity.prePersist();

        assertThat(entity.getRecordedAt()).isNotNull();
    }

    @Test
    void should_create_via_factory_method() {
        UUID userId = UUID.randomUUID();
        ConsentLogEntity entity = ConsentLogEntity.create(userId, ConsentType.TERMS_OF_SERVICE, true, "1.2.3.4",
                "Agent");

        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getConsentType()).isEqualTo(ConsentType.TERMS_OF_SERVICE);
        assertThat(entity.isConsentGiven()).isTrue();
        assertThat(entity.getIpAddress()).isEqualTo("1.2.3.4");
        assertThat(entity.getUserAgent()).isEqualTo("Agent");
        assertThat(entity.getRecordedAt()).isNotNull();
    }
}
