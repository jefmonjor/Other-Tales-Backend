package com.othertales.common.infrastructure.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    /**
     * Root health check endpoint for Cloud Run.
     * Returns simple "OK" for fast health check responses.
     */
    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("OK");
    }

    /**
     * Detailed health check endpoint.
     */
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "other-tales-api",
                "timestamp", Instant.now()
        ));
    }

    /**
     * JWT diagnostic endpoint. Returns decoded token claims if auth succeeds.
     * Helps Flutter devs verify their token is sent and parsed correctly.
     */
    @GetMapping("/api/v1/auth/check")
    public ResponseEntity<Map<String, Object>> authCheck(@AuthenticationPrincipal Jwt jwt) {
        var result = new LinkedHashMap<String, Object>();
        result.put("status", "AUTHENTICATED");
        result.put("sub", jwt.getSubject());
        result.put("iss", jwt.getClaimAsString("iss"));
        result.put("email", jwt.getClaimAsString("email"));
        result.put("iat", jwt.getIssuedAt());
        result.put("exp", jwt.getExpiresAt());
        result.put("alg", jwt.getHeaders().get("alg"));
        result.put("kid", jwt.getHeaders().get("kid"));
        result.put("timestamp", Instant.now());
        return ResponseEntity.ok(result);
    }
}
