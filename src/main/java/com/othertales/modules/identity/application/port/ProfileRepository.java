package com.othertales.modules.identity.application.port;

import com.othertales.modules.identity.domain.Profile;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository {

    Profile save(Profile profile);

    Optional<Profile> findById(UUID id);

    Optional<Profile> findByEmail(String email);

    boolean existsByEmail(String email);
}
