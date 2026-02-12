package com.othertales.modules.writing.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.othertales.modules.writing.application.dto.CreateProjectRequest;
import com.othertales.modules.writing.application.dto.ProjectListResponse;
import com.othertales.modules.writing.application.dto.ProjectResponse;
import com.othertales.modules.writing.application.usecase.ProjectService;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc
class ProjectControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ProjectService projectService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void should_return_list_of_projects_empty() throws Exception {
                ProjectListResponse listResponse = new ProjectListResponse(
                                Collections.emptyList(), 0, 10, 0, 0);

                when(projectService.listByUser(any(), anyInt(), anyInt(), any()))
                                .thenReturn(listResponse);

                mockMvc.perform(get("/api/v1/projects")
                                .with(jwt().jwt(builder -> builder.subject(UUID.randomUUID().toString()))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content").isEmpty());
        }

        @Test
        void should_create_project_successfully() throws Exception {
                CreateProjectRequest request = new CreateProjectRequest("My Project", "Syn", "Fantasy", 50000);
                ProjectResponse response = new ProjectResponse(
                                UUID.randomUUID(), "My Project", "Syn", "Fantasy", 0, 50000, null, "DRAFT",
                                Instant.now(), Instant.now());

                when(projectService.create(any(), any(CreateProjectRequest.class)))
                                .thenReturn(response);

                mockMvc.perform(post("/api/v1/projects")
                                .with(jwt().jwt(builder -> builder.subject(UUID.randomUUID().toString())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());
        }

        @Test
        void get_should_return_200_if_found() throws Exception {
                UUID projectId = UUID.randomUUID();
                ProjectResponse response = new ProjectResponse(
                                projectId, "Title", "Syn", "Gen", 0, 50000, null, "DRAFT", Instant.now(),
                                Instant.now());

                when(projectService.getById(eq(projectId), any()))
                                .thenReturn(response);

                mockMvc.perform(get("/api/v1/projects/{id}", projectId)
                                .with(jwt().jwt(builder -> builder.subject(UUID.randomUUID().toString()))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(projectId.toString()));
        }

        @Test
        void get_should_return_404_if_not_found() throws Exception {
                UUID projectId = UUID.randomUUID();

                when(projectService.getById(eq(projectId), any()))
                                .thenThrow(new ProjectNotFoundException(projectId));

                mockMvc.perform(get("/api/v1/projects/{id}", projectId)
                                .with(jwt().jwt(builder -> builder.subject(UUID.randomUUID().toString()))))
                                .andExpect(status().isNotFound());
        }

        @Test
        void create_should_return_400_if_title_blank() throws Exception {
                CreateProjectRequest request = new CreateProjectRequest("", "Syn", "Fantasy", 50000);

                mockMvc.perform(post("/api/v1/projects")
                                .with(jwt().jwt(builder -> builder.subject(UUID.randomUUID().toString())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }
}
