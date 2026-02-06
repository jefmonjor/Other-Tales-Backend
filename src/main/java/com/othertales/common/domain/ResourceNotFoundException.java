package com.othertales.common.domain;

import java.util.UUID;

/**
 * Base exception for all "not found" domain errors.
 * Allows GlobalExceptionHandler to handle all modules without direct imports.
 */
public abstract class ResourceNotFoundException extends RuntimeException {

    private final String errorCode;

    protected ResourceNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
