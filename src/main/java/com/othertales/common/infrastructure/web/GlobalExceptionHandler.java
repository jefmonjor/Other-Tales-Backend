package com.othertales.common.infrastructure.web;

import com.othertales.common.domain.AccessDeniedException;
import com.othertales.common.domain.BusinessException;
import com.othertales.common.domain.ErrorCodes;
import com.othertales.common.domain.ResourceNotFoundException;
import com.othertales.common.domain.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

/**
 * Global exception handler returning RFC 7807 (ProblemDetails) responses.
 *
 * AUDIT FIX #8 (FASE 2.4): Eliminated all direct imports from modules.identity
 * and modules.writing. Now uses base exception classes from common.domain only.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.debug("Resource not found: {}", ex.getMessage());
        return buildProblem(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getErrorCode(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleDomainAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildProblem(HttpStatus.FORBIDDEN, "Access Denied", ex.getErrorCode(), request);
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleDomainValidation(ValidationException ex, HttpServletRequest request) {
        log.debug("Validation error: {}", ex.getMessage());
        return buildProblem(HttpStatus.BAD_REQUEST, "Validation Error", ex.getErrorCode(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Business error: {}", ex.getErrorCode());
        return buildProblem(HttpStatus.BAD_REQUEST, "Business Rule Violation", ex.getErrorCode(), request);
    }

    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwtException(JwtException ex, HttpServletRequest request) {
        log.warn("JWT validation failed: {}", ex.getMessage());
        return buildProblem(HttpStatus.UNAUTHORIZED, "Invalid Token", ErrorCodes.AUTH_INVALID_TOKEN, request);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleSpringAccessDenied(
            org.springframework.security.access.AccessDeniedException ex,
            HttpServletRequest request
    ) {
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

    private ProblemDetail buildProblem(HttpStatus status, String title, String code, HttpServletRequest request) {
        var problem = ProblemDetail.forStatus(status);
        problem.setType(URI.create("about:blank"));
        problem.setTitle(title);
        problem.setDetail(code);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("code", code);
        return problem;
    }

    private String mapValidationCode(String validationCode) {
        if (validationCode == null) {
            return ErrorCodes.VALIDATION_FIELD_INVALID;
        }
        return switch (validationCode) {
            case "NotNull", "NotBlank", "NotEmpty" -> ErrorCodes.VALIDATION_FIELD_REQUIRED;
            case "Email" -> ErrorCodes.PROFILE_INVALID_EMAIL;
            case "Pattern", "Size", "Min", "Max" -> ErrorCodes.VALIDATION_FIELD_INVALID;
            default -> ErrorCodes.VALIDATION_FIELD_INVALID;
        };
    }

    public record FieldError(String field, String code) {}
}
