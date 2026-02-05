package com.othertales.modules.writing.infrastructure.web;

import com.othertales.modules.writing.application.dto.ChapterResponse;
import com.othertales.modules.writing.application.dto.SaveChapterRequest;
import com.othertales.modules.writing.application.usecase.ChapterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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
            @RequestHeader("X-User-Id") UUID userId
    ) {
        var chapters = chapterService.getChaptersByProjectId(projectId, userId);
        return ResponseEntity.ok(chapters);
    }

    @PostMapping("/chapters")
    public ResponseEntity<ChapterResponse> saveChapter(
            @Valid @RequestBody SaveChapterRequest request,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        var chapter = chapterService.createOrUpdateChapter(request, userId);

        var status = request.id() != null ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(chapter);
    }
}
