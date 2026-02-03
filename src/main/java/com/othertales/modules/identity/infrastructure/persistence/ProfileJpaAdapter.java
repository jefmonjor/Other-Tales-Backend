package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.domain.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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
        var entity = mapper.toEntity(profile);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Profile> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
