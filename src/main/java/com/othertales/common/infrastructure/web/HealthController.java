package com.othertales.common.infrastructure.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
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
}
