package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.domain.PlanType;
import com.othertales.modules.identity.domain.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileJpaAdapterTest {

    @Mock
    private ProfileJpaRepository jpaRepository;

    private ProfileMapper mapper;
    private ProfileJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = new ProfileMapper();
        adapter = new ProfileJpaAdapter(jpaRepository, mapper);
    }

    @Test
    void save_should_map_domain_to_entity_and_save() {
        // Given
        UUID id = UUID.randomUUID();
        Profile domainProfile = Profile.create(id, "test@example.com", "Test User");
        domainProfile.updateAvatarUrl("http://avatar.url");

        // Mock finding existing entity as empty (new profile)
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        // Use ArgumentCaptor to verify mapping
        org.mockito.ArgumentCaptor<ProfileEntity> entityCaptor = org.mockito.ArgumentCaptor
                .forClass(ProfileEntity.class);

        // Mock save to return what is passed
        when(jpaRepository.save(any(ProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Profile savedProfile = adapter.save(domainProfile);

        // Then
        verify(jpaRepository).save(entityCaptor.capture());
        ProfileEntity capturedEntity = entityCaptor.getValue();

        assertThat(capturedEntity.getId()).isEqualTo(id);
        assertThat(capturedEntity.getEmail()).isEqualTo("test@example.com");
        assertThat(capturedEntity.getFullName()).isEqualTo("Test User");
        assertThat(capturedEntity.getAvatarUrl()).isEqualTo("http://avatar.url");
        assertThat(capturedEntity.getPlanType()).isEqualTo(ProfileEntity.PlanTypeEntity.FREE);

        assertThat(savedProfile).isNotNull();
        assertThat(savedProfile.getId()).isEqualTo(id);
    }

    @Test
    void findById_should_map_entity_to_domain() {
        // Given
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        ProfileEntity entity = new ProfileEntity();
        entity.setId(id);
        entity.setEmail("stored@example.com");
        entity.setFullName("Stored User");
        entity.setPlanType(ProfileEntity.PlanTypeEntity.PRO);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setVersion(1L);

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        // When
        Optional<Profile> result = adapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getEmail()).isEqualTo("stored@example.com");
        assertThat(result.get().getPlanType()).isEqualTo(PlanType.PRO);
    }

    @Test
    void findByEmail_should_return_profile() {
        String email = "find@example.com";
        ProfileEntity entity = new ProfileEntity();
        entity.setEmail(email);

        when(jpaRepository.findByEmail(email)).thenReturn(Optional.of(entity));

        Optional<Profile> result = adapter.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }
}
