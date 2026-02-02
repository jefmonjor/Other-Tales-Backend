package com.othertales.modules.identity.infrastructure.config;

import com.othertales.modules.identity.application.port.PasswordEncoder;
import com.othertales.modules.identity.application.port.UserRepository;
import com.othertales.modules.identity.application.usecase.ForgotPasswordUseCase;
import com.othertales.modules.identity.application.usecase.LoginUserUseCase;
import com.othertales.modules.identity.application.usecase.RegisterUserUseCase;
import com.othertales.modules.identity.application.usecase.ResetPasswordUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return new RegisterUserUseCase(userRepository, passwordEncoder);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return new LoginUserUseCase(userRepository, passwordEncoder);
    }

    @Bean
    public ForgotPasswordUseCase forgotPasswordUseCase(UserRepository userRepository) {
        return new ForgotPasswordUseCase(userRepository);
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase() {
        return new ResetPasswordUseCase();
    }
}
