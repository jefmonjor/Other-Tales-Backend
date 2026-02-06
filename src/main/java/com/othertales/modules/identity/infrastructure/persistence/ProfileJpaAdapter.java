package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.domain.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * AUDIT FIX #3 (FASE 1.3): save() now checks if entity exists and
 * reuses it to preserve isNew=false for updates.
 */
@Repository
public class ProfileJpaAdapter implements ProfileRepository {

    private final ProfileJpaRepository jpaRepository;
    private final ProfileMapper mapper;

    public ProfileJpaAdapter(ProfileJpaRepository jpaRepository, ProfileMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Profile save(Profile profile) {
        var existingEntity = jpaRepository.findById(profile.getId()).orElse(null);
        var entity = existingEntity != null
                ? mapper.toEntity(profile, existingEntity)
                : mapper.toEntity(profile);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Profile> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
