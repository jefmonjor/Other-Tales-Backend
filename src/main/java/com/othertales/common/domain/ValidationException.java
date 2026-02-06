package com.othertales.common.domain;

/**
 * Base exception for all domain validation errors.
 * Allows GlobalExceptionHandler to handle all modules without direct imports.
 */
public abstract class ValidationException extends RuntimeException {

    private final String errorCode;

    protected ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
