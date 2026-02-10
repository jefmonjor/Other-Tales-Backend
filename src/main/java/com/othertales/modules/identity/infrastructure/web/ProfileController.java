package com.othertales.modules.identity.infrastructure.web;

import com.othertales.modules.identity.application.dto.ProfileResponse;
import com.othertales.modules.identity.application.dto.UpdateProfileRequest;
import com.othertales.modules.identity.application.usecase.GetCurrentProfileUseCase;
import com.othertales.modules.identity.application.usecase.UpdateProfileUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final GetCurrentProfileUseCase getCurrentProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    public ProfileController(
            GetCurrentProfileUseCase getCurrentProfileUseCase,
            UpdateProfileUseCase updateProfileUseCase
    ) {
        this.getCurrentProfileUseCase = getCurrentProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getCurrentProfile(@AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        var email = jwt.getClaimAsString("email");
        var userMetadata = jwt.getClaimAsMap("user_metadata");
        var fullName = userMetadata != null ? (String) userMetadata.get("full_name") : null;
        var profile = getCurrentProfileUseCase.execute(userId, email, fullName);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var userId = UUID.fromString(jwt.getSubject());
        var profile = updateProfileUseCase.execute(userId, request);
        return ResponseEntity.ok(profile);
    }
}
