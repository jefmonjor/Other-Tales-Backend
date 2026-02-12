package com.othertales.modules.writing.infrastructure.web;

import com.othertales.modules.writing.application.dto.CharacterResponse;
import com.othertales.modules.writing.application.dto.CreateCharacterRequest;
import com.othertales.modules.writing.application.dto.UpdateCharacterRequest;
import com.othertales.modules.writing.application.usecase.CharacterService;
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
@RequestMapping("/api/v1/projects/{projectId}/characters")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CharacterResponse> create(
            @PathVariable UUID projectId,
            @RequestPart("data") @Valid CreateCharacterRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Jwt jwt) throws IOException {
        var userId = extractUserId(jwt);
        byte[] imageBytes = image != null ? image.getBytes() : null;
        String contentType = image != null ? image.getContentType() : null;

        var response = characterService.createCharacter(projectId, request, imageBytes, contentType, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CharacterResponse>> list(
            @PathVariable UUID projectId,
            Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = extractUserId(jwt);
        var response = characterService.getCharactersByProjectId(projectId, pageable, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{characterId}")
    public ResponseEntity<CharacterResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID characterId,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = extractUserId(jwt);
        var response = characterService.getCharacterById(characterId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{characterId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CharacterResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID characterId,
            @RequestPart("data") @Valid UpdateCharacterRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Jwt jwt) throws IOException {
        var userId = extractUserId(jwt);
        byte[] imageBytes = image != null ? image.getBytes() : null;
        String contentType = image != null ? image.getContentType() : null;

        var response = characterService.updateCharacter(characterId, request, imageBytes, contentType, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{characterId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID characterId,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = extractUserId(jwt);
        characterService.deleteCharacter(characterId, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
