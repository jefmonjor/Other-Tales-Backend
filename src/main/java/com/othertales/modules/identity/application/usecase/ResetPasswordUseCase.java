package com.othertales.modules.identity.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetPasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordUseCase.class);

    public ResetPasswordUseCase() {
        // Dependencies will be added when real implementation is needed
    }

    public void execute(Command command) {
        // Mock implementation - real token validation and password reset will be implemented later
        log.info("Simulating password reset with token [{}]", command.token());
        // Always return success for mock implementation
    }

    public record Command(String token, String newPassword) {}
}
