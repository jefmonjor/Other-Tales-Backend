package com.othertales.modules.identity.application.usecase;

import com.othertales.modules.identity.application.port.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ForgotPasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordUseCase.class);

    private final UserRepository userRepository;

    public ForgotPasswordUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(Command command) {
        var userOptional = userRepository.findByEmail(command.email());

        if (userOptional.isPresent()) {
            var mockToken = UUID.randomUUID().toString().substring(0, 8);
            log.info("Simulating email sent to [{}] with token [{}]", command.email(), mockToken);
        } else {
            log.debug("Forgot password requested for non-existent email: {}", command.email());
        }
        // Always return success to prevent email enumeration attacks
    }

    public record Command(String email) {}
}
