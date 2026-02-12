package com.othertales.modules.writing.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.othertales.modules.writing.application.dto.ChapterResponse;
import com.othertales.modules.writing.application.dto.CreateChapterRequest;
import com.othertales.modules.writing.application.dto.ReorderChaptersRequest;
import com.othertales.modules.writing.application.dto.UpdateChapterRequest;
import com.othertales.modules.writing.application.usecase.ChapterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChapterController.class)
@AutoConfigureMockMvc
class ChapterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChapterService chapterService;

    @Test
    void getChaptersByProject_should_return_list() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ChapterResponse response = createChapterResponse();

        when(chapterService.getChaptersByProjectId(eq(projectId), eq(userId))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/projects/{projectId}/chapters", projectId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Chapter"));
    }

    @Test
    void createChapter_should_return_201() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CreateChapterRequest request = new CreateChapterRequest("New Chapter", "Content", null);
        ChapterResponse response = createChapterResponse();

        when(chapterService.createChapter(eq(projectId), any(), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/api/v1/projects/{projectId}/chapters", projectId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Chapter"));
    }

    @Test
    void getChapter_should_return_chapter() throws Exception {
        UUID chapterId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ChapterResponse response = createChapterResponse();

        when(chapterService.getChapterById(eq(chapterId), eq(userId))).thenReturn(response);

        mockMvc.perform(get("/api/v1/chapters/{chapterId}", chapterId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Chapter"));
    }

    @Test
    void updateChapter_should_return_updated() throws Exception {
        UUID chapterId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UpdateChapterRequest request = new UpdateChapterRequest("Updated", "Content", "PUBLISHED");
        ChapterResponse response = createChapterResponse();

        when(chapterService.updateChapter(eq(chapterId), any(), eq(userId))).thenReturn(response);

        mockMvc.perform(put("/api/v1/chapters/{chapterId}", chapterId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void reorderChapters_should_return_list() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReorderChaptersRequest request = new ReorderChaptersRequest(List.of(UUID.randomUUID()));

        when(chapterService.reorderChapters(eq(projectId), any(), eq(userId)))
                .thenReturn(List.of(createChapterResponse()));

        mockMvc.perform(patch("/api/v1/projects/{projectId}/chapters/reorder", projectId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteChapter_should_return_204() throws Exception {
        UUID chapterId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/chapters/{chapterId}", chapterId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                .andExpect(status().isNoContent());

        verify(chapterService).deleteChapter(eq(chapterId), eq(userId));
    }

    private ChapterResponse createChapterResponse() {
        return new ChapterResponse(UUID.randomUUID(), UUID.randomUUID(), "Test Chapter", "Content", 1, 100, "DRAFT",
                Instant.now(), Instant.now());
    }
}
