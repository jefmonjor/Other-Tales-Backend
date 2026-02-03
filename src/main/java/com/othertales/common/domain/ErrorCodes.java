package com.othertales.common.domain;

/**
 * Centralized error codes for i18n.
 * Frontend will map these codes to localized messages.
 */
public final class ErrorCodes {

    private ErrorCodes() {}

    // Auth & Profile errors
    public static final String AUTH_INVALID_TOKEN = "AUTH_INVALID_TOKEN";
    public static final String AUTH_TOKEN_EXPIRED = "AUTH_TOKEN_EXPIRED";
    public static final String AUTH_UNAUTHORIZED = "AUTH_UNAUTHORIZED";

    // Profile errors
    public static final String PROFILE_NOT_FOUND = "PROFILE_NOT_FOUND";
    public static final String PROFILE_EMAIL_EXISTS = "PROFILE_EMAIL_EXISTS";
    public static final String PROFILE_INVALID_EMAIL = "PROFILE_INVALID_EMAIL";
    public static final String PROFILE_INVALID_NAME = "PROFILE_INVALID_NAME";

    // Project errors
    public static final String PROJECT_NOT_FOUND = "PROJECT_NOT_FOUND";
    public static final String PROJECT_INVALID_TITLE = "PROJECT_INVALID_TITLE";
    public static final String PROJECT_ACCESS_DENIED = "PROJECT_ACCESS_DENIED";

    // Validation errors
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String VALIDATION_FIELD_REQUIRED = "VALIDATION_FIELD_REQUIRED";
    public static final String VALIDATION_FIELD_INVALID = "VALIDATION_FIELD_INVALID";

    // Generic errors
    public static final String DATA_CONFLICT = "DATA_CONFLICT";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
}
