package com.othertales.modules.writing.infrastructure.web;

import com.othertales.modules.writing.application.dto.ChapterResponse;
import com.othertales.modules.writing.application.dto.CreateChapterRequest;
import com.othertales.modules.writing.application.dto.ReorderChaptersRequest;
import com.othertales.modules.writing.application.dto.UpdateChapterRequest;
import com.othertales.modules.writing.application.usecase.ChapterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * AUDIT FIX #2 (FASE 1.2): Replaced @RequestHeader("X-User-Id") with @AuthenticationPrincipal Jwt.
 * AUDIT FIX #5 (FASE 1.5): Aligned with OpenAPI spec - separate POST/PUT/DELETE/GET endpoints.
 */
@RestController
@RequestMapping("/api/v1")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/projects/{projectId}/chapters")
    public ResponseEntity<List<ChapterResponse>> getChaptersByProject(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var userId = extractUserId(jwt);
        var chapters = chapterService.getChaptersByProjectId(projectId, userId);
        return ResponseEntity.ok(chapters);
    }

    @PostMapping("/projects/{projectId}/chapters")
    public ResponseEntity<ChapterResponse> createChapter(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateChapterRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var userId = extractUserId(jwt);
        var chapter = chapterService.createChapter(projectId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(chapter);
    }

    @GetMapping("/chapters/{chapterId}")
    public ResponseEntity<ChapterResponse> getChapter(
            @PathVariable UUID chapterId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var userId = extractUserId(jwt);
        var chapter = chapterService.getChapterById(chapterId, userId);
        return ResponseEntity.ok(chapter);
    }

    @PutMapping("/chapters/{chapterId}")
    public ResponseEntity<ChapterResponse> updateChapter(
            @PathVariable UUID chapterId,
            @Valid @RequestBody UpdateChapterRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var userId = extractUserId(jwt);
        var chapter = chapterService.updateChapter(chapterId, request, userId);
        return ResponseEntity.ok(chapter);
    }

    @PatchMapping("/projects/{projectId}/chapters/reorder")
    public ResponseEntity<List<ChapterResponse>> reorderChapters(
            @PathVariable UUID projectId,
            @Valid @RequestBody ReorderChaptersRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var userId = extractUserId(jwt);
        var chapters = chapterService.reorderChapters(projectId, request, userId);
        return ResponseEntity.ok(chapters);
    }

    @DeleteMapping("/chapters/{chapterId}")
    public ResponseEntity<Void> deleteChapter(
            @PathVariable UUID chapterId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var userId = extractUserId(jwt);
        chapterService.deleteChapter(chapterId, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
