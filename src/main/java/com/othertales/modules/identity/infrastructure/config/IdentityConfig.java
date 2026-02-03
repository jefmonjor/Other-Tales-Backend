package com.othertales.modules.identity.infrastructure.config;

import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.application.usecase.GetCurrentProfileUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityConfig {

    @Bean
    public GetCurrentProfileUseCase getCurrentProfileUseCase(ProfileRepository profileRepository) {
        return new GetCurrentProfileUseCase(profileRepository);
    }
}
