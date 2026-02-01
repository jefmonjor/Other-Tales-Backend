package com.othertales.modules.identity.application.usecase;

import com.othertales.modules.identity.application.port.PasswordEncoder;
import com.othertales.modules.identity.application.port.UserRepository;
import com.othertales.modules.identity.domain.InvalidCredentialsException;
import com.othertales.modules.identity.domain.User;

public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(Command command) {
        var user = userRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    public record Command(
            String email,
            String password
    ) {}
}
