package com.othertales.modules.writing.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.othertales.modules.writing.application.dto.CreateProjectRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Sql(scripts = "/sql/test-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DisplayName("Project API Integration Tests")
class ProjectIntegrationTest {

    private static final String API_PROJECTS = "/api/v1/projects";
    private static final String X_USER_ID_HEADER = "X-User-Id";

    // Predefined test user IDs (must match test-users.sql)
    private static final UUID TEST_USER_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TEST_USER_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID OWNER_USER = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID OTHER_USER = UUID.fromString("44444444-4444-4444-4444-444444444444");

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("othertales_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /api/v1/projects")
    class CreateProjectTests {

        @Test
        @WithMockUser
        @DisplayName("Happy Path: should create project with title and genre and return 201")
        void shouldCreateProjectWithTitleAndGenre() throws Exception {
            var request = new CreateProjectRequest(
                    "My Fantasy Novel",
                    "An epic tale of adventure",
                    "Fantasy",
                    80000
            );

            mockMvc.perform(post(API_PROJECTS)
                            .header(X_USER_ID_HEADER, TEST_USER_1.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.title", is("My Fantasy Novel")))
                    .andExpect(jsonPath("$.genre", is("Fantasy")))
                    .andExpect(jsonPath("$.synopsis", is("An epic tale of adventure")))
                    .andExpect(jsonPath("$.targetWordCount", is(80000)))
                    .andExpect(jsonPath("$.currentWordCount", is(0)))
                    .andExpect(jsonPath("$.status", is("DRAFT")));
        }

        @Test
        @WithMockUser
        @DisplayName("Validation: should return 400 when title is missing")
        void shouldReturn400WhenTitleIsMissing() throws Exception {
            var requestJson = """
                    {
                        "synopsis": "A story without title",
                        "genre": "Mystery",
                        "targetWordCount": 50000
                    }
                    """;

            mockMvc.perform(post(API_PROJECTS)
                            .header(X_USER_ID_HEADER, TEST_USER_1.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", notNullValue()))
                    .andExpect(jsonPath("$.title", is("Validation Error")))
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].field", is("title")));
        }

        @Test
        @WithMockUser
        @DisplayName("Validation: should return 400 when title is empty string")
        void shouldReturn400WhenTitleIsEmpty() throws Exception {
            var request = new CreateProjectRequest(
                    "",
                    "Synopsis here",
                    "Horror",
                    50000
            );

            mockMvc.perform(post(API_PROJECTS)
                            .header(X_USER_ID_HEADER, TEST_USER_1.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is("Validation Error")))
                    .andExpect(jsonPath("$.errors[0].field", is("title")));
        }

        @Test
        @WithMockUser
        @DisplayName("Validation: should return 400 when targetWordCount is negative")
        void shouldReturn400WhenTargetWordCountIsNegative() throws Exception {
            var requestJson = """
                    {
                        "title": "Valid Title",
                        "genre": "Sci-Fi",
                        "targetWordCount": -100
                    }
                    """;

            mockMvc.perform(post(API_PROJECTS)
                            .header(X_USER_ID_HEADER, TEST_USER_1.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].field", is("targetWordCount")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/projects")
    class ListProjectsTests {

        @Test
        @WithMockUser
        @DisplayName("Retrieval: should return created project in list")
        void shouldReturnCreatedProjectInList() throws Exception {
            var request = new CreateProjectRequest(
                    "Retrieval Test Novel",
                    "Testing retrieval functionality",
                    "Romance",
                    60000
            );

            // First create a project
            mockMvc.perform(post(API_PROJECTS)
                            .header(X_USER_ID_HEADER, TEST_USER_1.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // Then retrieve and verify
            mockMvc.perform(get(API_PROJECTS)
                            .header(X_USER_ID_HEADER, TEST_USER_1.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].title", is("Retrieval Test Novel")))
                    .andExpect(jsonPath("$.content[0].genre", is("Romance")))
                    .andExpect(jsonPath("$.content[0].targetWordCount", is(60000)))
                    .andExpect(jsonPath("$.content[0].status", is("DRAFT")));
        }

        @Test
        @WithMockUser
        @DisplayName("should return empty list for user with no projects")
        void shouldReturnEmptyListForNewUser() throws Exception {
            // OTHER_USER has no projects created in tests
            mockMvc.perform(get(API_PROJECTS)
                            .header(X_USER_ID_HEADER, OTHER_USER.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)));
        }

        @Test
        @WithMockUser
        @DisplayName("should isolate projects by user")
        void shouldIsolateProjectsByUser() throws Exception {
            // User1 creates a project
            var request1 = new CreateProjectRequest("User1 Novel", null, "Thriller", 50000);
            mockMvc.perform(post(API_PROJECTS)
                            .header(X_USER_ID_HEADER, TEST_USER_1.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request1)))
                    .andExpect(status().isCreated());

            // User2 creates a project
            var request2 = new CreateProjectRequest("User2 Novel", null, "Comedy", 40000);
            mockMvc.perform(post(API_PROJECTS)
                            .header(X_USER_ID_HEADER, TEST_USER_2.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request2)))
                    .andExpect(status().isCreated());

            // User1 should see their projects (may include projects from other tests)
            mockMvc.perform(get(API_PROJECTS)
                            .header(X_USER_ID_HEADER, TEST_USER_1.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[?(@.title == 'User1 Novel')]").exists());

            // User2 should see their projects
            mockMvc.perform(get(API_PROJECTS)
                            .header(X_USER_ID_HEADER, TEST_USER_2.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[?(@.title == 'User2 Novel')]").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/projects/{id}")
    class GetProjectByIdTests {

        @Test
        @WithMockUser
        @DisplayName("should return project by ID for owner")
        void shouldReturnProjectByIdForOwner() throws Exception {
            var request = new CreateProjectRequest(
                    "Specific Project",
                    "Details about this project",
                    "Historical Fiction",
                    75000
            );

            // Create project and extract ID
            var createResult = mockMvc.perform(post(API_PROJECTS)
                            .header(X_USER_ID_HEADER, OWNER_USER.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            var responseJson = createResult.getResponse().getContentAsString();
            var projectId = objectMapper.readTree(responseJson).get("id").asText();

            // Retrieve by ID
            mockMvc.perform(get(API_PROJECTS + "/" + projectId)
                            .header(X_USER_ID_HEADER, OWNER_USER.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(projectId)))
                    .andExpect(jsonPath("$.title", is("Specific Project")))
                    .andExpect(jsonPath("$.synopsis", is("Details about this project")))
                    .andExpect(jsonPath("$.genre", is("Historical Fiction")))
                    .andExpect(jsonPath("$.targetWordCount", is(75000)));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 for non-existent project")
        void shouldReturn404ForNonExistentProject() throws Exception {
            var nonExistentId = UUID.randomUUID();

            mockMvc.perform(get(API_PROJECTS + "/" + nonExistentId)
                            .header(X_USER_ID_HEADER, TEST_USER_1.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title", is("Project Not Found")));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 when accessing another user's project")
        void shouldReturn404WhenAccessingOtherUsersProject() throws Exception {
            // Owner creates project
            var request = new CreateProjectRequest("Owner's Secret Project", null, "Mystery", 50000);
            var createResult = mockMvc.perform(post(API_PROJECTS)
                            .header(X_USER_ID_HEADER, OWNER_USER.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            var projectId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                    .get("id").asText();

            // Other user tries to access
            mockMvc.perform(get(API_PROJECTS + "/" + projectId)
                            .header(X_USER_ID_HEADER, OTHER_USER.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }
}
