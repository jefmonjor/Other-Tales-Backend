package com.othertales.modules.identity.application.usecase;

import com.othertales.modules.identity.application.port.PasswordEncoder;
import com.othertales.modules.identity.application.port.UserRepository;
import com.othertales.modules.identity.domain.EmailAlreadyExistsException;
import com.othertales.modules.identity.domain.User;

public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(Command command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        var passwordHash = passwordEncoder.encode(command.password());
        var user = User.create(command.email(), passwordHash, command.fullName());

        return userRepository.save(user);
    }

    public record Command(
            String email,
            String password,
            String fullName
    ) {}
}
