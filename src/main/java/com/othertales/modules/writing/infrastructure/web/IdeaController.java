package com.othertales.modules.writing.infrastructure.web;

import com.othertales.modules.writing.application.dto.CreateIdeaRequest;
import com.othertales.modules.writing.application.dto.IdeaResponse;
import com.othertales.modules.writing.application.dto.UpdateIdeaRequest;
import com.othertales.modules.writing.application.usecase.IdeaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/ideas")
public class IdeaController {

    private final IdeaService ideaService;

    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    @PostMapping
    public ResponseEntity<IdeaResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateIdeaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = extractUserId(jwt);
        var response = ideaService.createIdea(projectId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<IdeaResponse>> list(
            @PathVariable UUID projectId,
            Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = extractUserId(jwt);
        var response = ideaService.getIdeasByProjectId(projectId, pageable, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{ideaId}")
    public ResponseEntity<IdeaResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID ideaId,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = extractUserId(jwt);
        var response = ideaService.getIdeaById(ideaId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{ideaId}")
    public ResponseEntity<IdeaResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID ideaId,
            @Valid @RequestBody UpdateIdeaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = extractUserId(jwt);
        var response = ideaService.updateIdea(ideaId, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{ideaId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID ideaId,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = extractUserId(jwt);
        ideaService.deleteIdea(ideaId, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
