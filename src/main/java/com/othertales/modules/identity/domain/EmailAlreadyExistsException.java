package com.othertales.modules.identity.domain;

public class EmailAlreadyExistsException extends RuntimeException {

    private final String email;

    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
