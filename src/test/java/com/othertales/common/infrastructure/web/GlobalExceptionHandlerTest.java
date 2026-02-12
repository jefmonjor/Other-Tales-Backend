package com.othertales.common.infrastructure.web;

import com.othertales.common.domain.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void should_handle_resource_not_found() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                // Handler uses error code as detail currently
                .andExpect(jsonPath("$.detail").value("TEST_NOT_FOUND"));
    }

    @Test
    void should_handle_illegal_argument() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Invalid Argument"));
    }

    @Test
    void should_handle_validation_errors() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[0].field").value("field"))
                .andExpect(jsonPath("$.errors[0].code").value("VALIDATION_FIELD_REQUIRED"));
    }

    @Test
    void should_handle_access_denied() throws Exception {
        mockMvc.perform(get("/test/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Access Denied"));
    }

    @Test
    void should_handle_type_mismatch() throws Exception {
        mockMvc.perform(get("/test/type-mismatch/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Argument Type"));
    }

    @Test
    void should_handle_business_exception() throws Exception {
        mockMvc.perform(get("/test/business"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Business Rule Violation"))
                .andExpect(jsonPath("$.detail").value("BUSINESS_ERROR"));
    }

    @Test
    void should_handle_data_integrity_violation_email() throws Exception {
        mockMvc.perform(get("/test/data-integrity-email"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Data Conflict"))
                .andExpect(jsonPath("$.detail").value("PROFILE_EMAIL_EXISTS"));
    }

    @RestController
    static class DummyController {
        @GetMapping("/test/not-found")
        void throwNotFound() {
            throw new TestResourceNotFoundException("Test resource not found");
        }

        @GetMapping("/test/business")
        void throwBusiness() {
            throw new com.othertales.common.domain.BusinessException("BUSINESS_ERROR");
        }

        @GetMapping("/test/data-integrity-email")
        void throwDataIntegrityEmail() {
            throw new org.springframework.dao.DataIntegrityViolationException("Duplicate key: email already exists");
        }

        @GetMapping("/test/illegal-argument")
        void throwIllegalArgument() {
            throw new IllegalArgumentException("Invalid argument passed");
        }

        @org.springframework.web.bind.annotation.PostMapping("/test/validation")
        void validate(@jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody TestDto dto) {
        }

        @GetMapping("/test/access-denied")
        void throwAccessDenied() {
            throw new org.springframework.security.access.AccessDeniedException("Access Denied");
        }

        @GetMapping("/test/type-mismatch/{id}")
        void throwTypeMismatch(@org.springframework.web.bind.annotation.PathVariable java.util.UUID id) {
        }
    }

    record TestDto(@jakarta.validation.constraints.NotNull String field) {
    }

    static class TestResourceNotFoundException extends ResourceNotFoundException {
        public TestResourceNotFoundException(String message) {
            super(message, "TEST_NOT_FOUND");
        }
    }
}
