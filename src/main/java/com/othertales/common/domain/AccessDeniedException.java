package com.othertales.common.domain;

/**
 * Base exception for all "access denied" domain errors.
 * Allows GlobalExceptionHandler to handle all modules without direct imports.
 */
public abstract class AccessDeniedException extends RuntimeException {

    private final String errorCode;

    protected AccessDeniedException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
