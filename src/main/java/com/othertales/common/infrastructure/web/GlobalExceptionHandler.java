package com.othertales.common.infrastructure.web;

import com.othertales.common.domain.BusinessException;
import com.othertales.common.domain.ErrorCodes;
import com.othertales.modules.identity.domain.ProfileNotFoundException;
import com.othertales.modules.writing.domain.ChapterAccessDeniedException;
import com.othertales.modules.writing.domain.ChapterNotFoundException;
import com.othertales.modules.writing.domain.InvalidProjectTitleException;
import com.othertales.modules.writing.domain.InvalidTargetWordCountException;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;

/**
 * Global exception handler returning RFC 7807 (ProblemDetails) responses.
 * Uses Spring Boot 3.x native ProblemDetail class.
 *
 * Custom properties added for Frontend i18n:
 * - "code": Error code for translation lookup
 * - "errors": List of field validation errors
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Business error: {}", ex.getErrorCode());

        return buildProblem(HttpStatus.BAD_REQUEST, "Business Rule Violation", ex.getErrorCode(), request);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ProblemDetail handleProfileNotFound(ProfileNotFoundException ex, HttpServletRequest request) {
        log.debug("Profile not found: {}", ex.getMessage());

        return buildProblem(HttpStatus.NOT_FOUND, "Profile Not Found", ErrorCodes.PROFILE_NOT_FOUND, request);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ProblemDetail handleProjectNotFound(ProjectNotFoundException ex, HttpServletRequest request) {
        log.debug("Project not found: {}", ex.getMessage());

        return buildProblem(HttpStatus.NOT_FOUND, "Project Not Found", ErrorCodes.PROJECT_NOT_FOUND, request);
    }

    @ExceptionHandler(ChapterNotFoundException.class)
    public ProblemDetail handleChapterNotFound(ChapterNotFoundException ex, HttpServletRequest request) {
        log.debug("Chapter not found: {}", ex.getMessage());

        return buildProblem(HttpStatus.NOT_FOUND, "Chapter Not Found", ErrorCodes.CHAPTER_NOT_FOUND, request);
    }

    @ExceptionHandler(ChapterAccessDeniedException.class)
    public ProblemDetail handleChapterAccessDenied(ChapterAccessDeniedException ex, HttpServletRequest request) {
        log.warn("Chapter access denied: {}", ex.getMessage());

        return buildProblem(HttpStatus.FORBIDDEN, "Access Denied", ErrorCodes.CHAPTER_ACCESS_DENIED, request);
    }

    @ExceptionHandler(InvalidProjectTitleException.class)
    public ProblemDetail handleInvalidProjectTitle(InvalidProjectTitleException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Invalid Project Title", ErrorCodes.PROJECT_INVALID_TITLE, request);
    }

    @ExceptionHandler(InvalidTargetWordCountException.class)
    public ProblemDetail handleInvalidTargetWordCount(InvalidTargetWordCountException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Invalid Word Count", ErrorCodes.PROJECT_INVALID_WORD_COUNT, request);
    }

    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwtException(JwtException ex, HttpServletRequest request) {
        log.warn("JWT validation failed: {}", ex.getMessage());

        return buildProblem(HttpStatus.UNAUTHORIZED, "Invalid Token", ErrorCodes.AUTH_INVALID_TOKEN, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.FORBIDDEN, "Access Denied", ErrorCodes.AUTH_UNAUTHORIZED, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(), mapValidationCode(error.getCode())))
                .toList();

        var problem = buildProblem(HttpStatus.BAD_REQUEST, "Validation Failed", ErrorCodes.VALIDATION_FAILED, request);
        problem.setProperty("errors", fieldErrors);
        return problem;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation: {}", ex.getMessage());

        var code = ErrorCodes.DATA_CONFLICT;
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("email")) {
            code = ErrorCodes.PROFILE_EMAIL_EXISTS;
        }

        return buildProblem(HttpStatus.CONFLICT, "Data Conflict", code, request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ErrorCodes.INTERNAL_ERROR, request);
    }

    /**
     * Build RFC 7807 ProblemDetail with custom "code" property for i18n.
     */
    private ProblemDetail buildProblem(HttpStatus status, String title, String code, HttpServletRequest request) {
        var problem = ProblemDetail.forStatus(status);
        problem.setType(URI.create("about:blank"));
        problem.setTitle(title);
        problem.setDetail(code); // Code as detail for quick reference
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("code", code); // Custom: i18n error code
        return problem;
    }

    private String mapValidationCode(String validationCode) {
        if (validationCode == null) {
            return ErrorCodes.VALIDATION_FIELD_INVALID;
        }
        return switch (validationCode) {
            case "NotNull", "NotBlank", "NotEmpty" -> ErrorCodes.VALIDATION_FIELD_REQUIRED;
            case "Email" -> ErrorCodes.PROFILE_INVALID_EMAIL;
            case "PROJECT_INVALID_TITLE" -> ErrorCodes.PROJECT_INVALID_TITLE;
            case "PROJECT_INVALID_GENRE" -> ErrorCodes.PROJECT_INVALID_GENRE;
            case "PROFILE_INVALID_NAME" -> ErrorCodes.PROFILE_INVALID_NAME;
            case "Pattern", "Size", "Min", "Max" -> ErrorCodes.VALIDATION_FIELD_INVALID;
            default -> ErrorCodes.VALIDATION_FIELD_INVALID;
        };
    }

    /**
     * Field validation error for RFC 7807 "errors" extension.
     */
    public record FieldError(String field, String code) {}
}
