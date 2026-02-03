package com.othertales.modules.identity.infrastructure.web;

import com.othertales.modules.identity.application.dto.ProfileResponse;
import com.othertales.modules.identity.application.usecase.GetCurrentProfileUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final GetCurrentProfileUseCase getCurrentProfileUseCase;

    public ProfileController(GetCurrentProfileUseCase getCurrentProfileUseCase) {
        this.getCurrentProfileUseCase = getCurrentProfileUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getCurrentProfile(@AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        var profile = getCurrentProfileUseCase.execute(userId);
        return ResponseEntity.ok(profile);
    }
}
