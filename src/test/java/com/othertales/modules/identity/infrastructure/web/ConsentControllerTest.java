package com.othertales.modules.identity.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.othertales.modules.identity.application.dto.ConsentResponse;
import com.othertales.modules.identity.application.dto.UpdateConsentRequest;
import com.othertales.modules.identity.application.port.ProfileRepository;
import com.othertales.modules.identity.application.usecase.UpdateConsentUseCase;
import com.othertales.modules.identity.domain.ConsentType;
import com.othertales.modules.identity.domain.PlanType;
import com.othertales.modules.identity.domain.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsentController.class)
@AutoConfigureMockMvc
class ConsentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UpdateConsentUseCase updateConsentUseCase;

    @MockitoBean
    private ProfileRepository profileRepository;

    @Test
    void getCurrentConsent_should_return_list() throws Exception {
        UUID userId = UUID.randomUUID();
        Profile profile = Profile.reconstitute(
                userId, "test@example.com", "Test User", null,
                PlanType.FREE, true, Instant.now(), true, Instant.now(), false, null,
                Instant.now(), Instant.now(), 0L);

        when(profileRepository.findById(userId)).thenReturn(Optional.of(profile));

        var jwt = SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(userId.toString()));

        mockMvc.perform(get("/api/v1/user/consent")
                .with(jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(ConsentType.values().length))
                .andExpect(jsonPath("$[0].granted").exists());
    }

    @Test
    void updateConsent_should_return_response() throws Exception {
        UUID userId = UUID.randomUUID();
        UpdateConsentRequest request = new UpdateConsentRequest(ConsentType.MARKETING_COMMUNICATIONS, true);
        ConsentResponse response = new ConsentResponse(ConsentType.MARKETING_COMMUNICATIONS, true, Instant.now());

        when(updateConsentUseCase.execute(eq(userId), any(), any(), any())).thenReturn(response);

        var jwt = SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(userId.toString()));

        mockMvc.perform(post("/api/v1/user/consent")
                .with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consentType").value("MARKETING_COMMUNICATIONS"))
                .andExpect(jsonPath("$.granted").value(true));
    }
}
