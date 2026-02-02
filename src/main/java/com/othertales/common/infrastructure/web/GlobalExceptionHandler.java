package com.othertales.common.infrastructure.web;

import com.othertales.modules.identity.domain.EmailAlreadyExistsException;
import com.othertales.modules.identity.domain.InvalidCredentialsException;
import com.othertales.modules.writing.domain.InvalidProjectTitleException;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problem.setType(URI.create("https://api.othertales.com/problems/email-already-exists"));
        problem.setTitle("Email Already Registered");
        return problem;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
        problem.setType(URI.create("https://api.othertales.com/problems/invalid-credentials"));
        problem.setTitle("Invalid Credentials");
        return problem;
    }

    @ExceptionHandler(InvalidProjectTitleException.class)
    public ProblemDetail handleInvalidProjectTitle(InvalidProjectTitleException ex) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problem.setType(URI.create("https://api.othertales.com/problems/invalid-project-title"));
        problem.setTitle("Invalid Project Title");
        return problem;
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ProblemDetail handleProjectNotFound(ProjectNotFoundException ex) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setType(URI.create("https://api.othertales.com/problems/project-not-found"));
        problem.setTitle("Project Not Found");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields"
        );
        problem.setType(URI.create("https://api.othertales.com/problems/validation-error"));
        problem.setTitle("Validation Error");

        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());

        var message = "A data constraint was violated";
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("email")) {
            message = "Email already exists";
        }

        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                message
        );
        problem.setType(URI.create("https://api.othertales.com/problems/data-conflict"));
        problem.setTitle("Data Conflict");
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        problem.setType(URI.create("https://api.othertales.com/problems/internal-error"));
        problem.setTitle("Internal Server Error");
        return problem;
    }

    public record FieldError(String field, String message) {}
}
