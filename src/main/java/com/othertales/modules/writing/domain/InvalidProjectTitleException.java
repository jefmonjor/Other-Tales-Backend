package com.othertales.modules.writing.domain;

public class InvalidProjectTitleException extends RuntimeException {

    public InvalidProjectTitleException() {
        super("Project title cannot be null or empty");
    }
}
