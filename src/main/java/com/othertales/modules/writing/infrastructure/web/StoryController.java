package com.othertales.modules.writing.infrastructure.web;

import com.othertales.modules.writing.application.dto.CreateStoryRequest;
import com.othertales.modules.writing.application.dto.StoryResponse;
import com.othertales.modules.writing.application.dto.UpdateStoryRequest;
import com.othertales.modules.writing.application.usecase.StoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoryResponse> create(
            @PathVariable UUID projectId,
            @RequestPart("data") @Valid CreateStoryRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Jwt jwt) throws IOException {
        var userId = extractUserId(jwt);
        byte[] imageBytes = image != null ? image.getBytes() : null;
        String contentType = image != null ? image.getContentType() : null;

        var response = storyService.createStory(projectId, request, imageBytes, contentType, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<StoryResponse>> list(
            @PathVariable UUID projectId,
            Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = extractUserId(jwt);
        var response = storyService.getStoriesByProjectId(projectId, pageable, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<StoryResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID storyId,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = extractUserId(jwt);
        var response = storyService.getStoryById(storyId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{storyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoryResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID storyId,
            @RequestPart("data") @Valid UpdateStoryRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Jwt jwt) throws IOException {
        var userId = extractUserId(jwt);
        byte[] imageBytes = image != null ? image.getBytes() : null;
        String contentType = image != null ? image.getContentType() : null;

        var response = storyService.updateStory(storyId, request, imageBytes, contentType, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID storyId,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = extractUserId(jwt);
        storyService.deleteStory(storyId, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
