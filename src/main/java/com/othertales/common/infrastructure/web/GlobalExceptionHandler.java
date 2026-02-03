package com.othertales.common.infrastructure.web;

import com.othertales.common.domain.BusinessException;
import com.othertales.common.domain.ErrorCodes;
import com.othertales.modules.identity.domain.ProfileNotFoundException;
import com.othertales.modules.writing.domain.InvalidProjectTitleException;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Global exception handler that returns error CODES instead of messages.
 * Frontend is responsible for i18n translation.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        log.warn("Business error: {}", ex.getErrorCode());

        var response = new ErrorResponse(ex.getErrorCode(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProfileNotFound(
            ProfileNotFoundException ex, HttpServletRequest request) {
        log.debug("Profile not found: {}", ex.getMessage());

        var response = new ErrorResponse(ErrorCodes.PROFILE_NOT_FOUND, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFound(
            ProjectNotFoundException ex, HttpServletRequest request) {
        log.debug("Project not found: {}", ex.getMessage());

        var response = new ErrorResponse(ErrorCodes.PROJECT_NOT_FOUND, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidProjectTitleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProjectTitle(
            InvalidProjectTitleException ex, HttpServletRequest request) {

        var response = new ErrorResponse(ErrorCodes.PROJECT_INVALID_TITLE, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(
            JwtException ex, HttpServletRequest request) {
        log.warn("JWT validation failed: {}", ex.getMessage());

        var response = new ErrorResponse(ErrorCodes.AUTH_INVALID_TOKEN, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        var response = new ErrorResponse(ErrorCodes.AUTH_UNAUTHORIZED, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        mapValidationCode(error.getCode())
                ))
                .toList();

        var response = new ErrorResponse(
                ErrorCodes.VALIDATION_FAILED,
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation: {}", ex.getMessage());

        var code = ErrorCodes.DATA_CONFLICT;
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("email")) {
            code = ErrorCodes.PROFILE_EMAIL_EXISTS;
        }

        var response = new ErrorResponse(code, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        var response = new ErrorResponse(ErrorCodes.INTERNAL_ERROR, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
}
