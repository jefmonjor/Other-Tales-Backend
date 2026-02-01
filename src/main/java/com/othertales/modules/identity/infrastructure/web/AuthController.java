package com.othertales.modules.identity.infrastructure.web;

import com.othertales.modules.identity.application.dto.AuthResponse;
import com.othertales.modules.identity.application.dto.RegisterRequest;
import com.othertales.modules.identity.application.usecase.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ProblemDetail;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        var command = new RegisterUserUseCase.Command(
                request.email(),
                request.password(),
                request.fullName()
        );

        var user = registerUserUseCase.execute(command);

        // TODO: Generate JWT token in future iteration
        var response = new AuthResponse(
                "token-placeholder-" + user.getId(),
                "Bearer",
                3600L
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/social/{provider}")
    public ResponseEntity<ProblemDetail> socialLogin(@PathVariable String provider) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_IMPLEMENTED,
                "Social login with " + provider + " is not implemented yet"
        );
        problem.setType(URI.create("https://api.othertales.com/problems/not-implemented"));
        problem.setTitle("Not Implemented");

        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(problem);
    }
}
