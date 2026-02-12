package com.othertales.modules.identity.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.othertales.modules.identity.application.dto.ProfileResponse;
import com.othertales.modules.identity.application.dto.UpdateProfileRequest;
import com.othertales.modules.identity.application.usecase.GetCurrentProfileUseCase;
import com.othertales.modules.identity.application.usecase.UpdateProfileUseCase;
import com.othertales.modules.identity.domain.PlanType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc // Enable Security Filters
class ProfileControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private GetCurrentProfileUseCase getCurrentProfileUseCase;

        @MockitoBean
        private UpdateProfileUseCase updateProfileUseCase;

        @Test
        void getCurrentProfile_should_return_profile() throws Exception {
                UUID userId = UUID.randomUUID();
                ProfileResponse response = new ProfileResponse(
                                userId, "test@example.com", "Test User", "http://avatar.url",
                                PlanType.FREE.name(), false, null, false, null, false, null,
                                Instant.now());

                when(getCurrentProfileUseCase.execute(eq(userId), any(), any())).thenReturn(response);

                // Mock JWT
                var jwt = SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(builder -> builder.subject(userId.toString())
                                                .claim("email", "test@example.com")
                                                .claim("user_metadata", Map.of("full_name", "Test User")));

                mockMvc.perform(get("/api/v1/profiles/me")
                                .with(jwt)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(userId.toString()))
                                .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        void updateProfile_should_return_updated_profile() throws Exception {
                UUID userId = UUID.randomUUID();
                UpdateProfileRequest request = new UpdateProfileRequest("New Name", "http://new.avatar");
                ProfileResponse response = new ProfileResponse(
                                userId, "test@example.com", "New Name", "http://new.avatar",
                                PlanType.FREE.name(), false, null, false, null, false, null,
                                Instant.now());

                when(updateProfileUseCase.execute(eq(userId), any(UpdateProfileRequest.class))).thenReturn(response);

                var jwt = SecurityMockMvcRequestPostProcessors.jwt().jwt(builder -> builder.subject(userId.toString()));

                mockMvc.perform(put("/api/v1/profiles/me")
                                .with(jwt)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.fullName").value("New Name"))
                                .andExpect(jsonPath("$.avatarUrl").value("http://new.avatar"));
        }
}
