package com.othertales.common.infrastructure.web;

import java.time.Instant;
import java.util.List;

/**
 * Standardized error response for i18n.
 * Contains error codes that Frontend will translate.
 */
public record ErrorResponse(
        String code,
        String path,
        Instant timestamp,
        List<FieldError> fieldErrors
) {
    public ErrorResponse(String code, String path) {
        this(code, path, Instant.now(), null);
    }

    public ErrorResponse(String code, String path, List<FieldError> fieldErrors) {
        this(code, path, Instant.now(), fieldErrors);
    }

    public record FieldError(String field, String code) {}
}
