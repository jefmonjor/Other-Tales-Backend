package com.othertales.modules.identity.domain;

public class UserNotFoundException extends RuntimeException {

    private final String email;

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
