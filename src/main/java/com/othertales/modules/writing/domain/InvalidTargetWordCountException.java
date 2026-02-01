package com.othertales.modules.writing.domain;

public class InvalidTargetWordCountException extends RuntimeException {

    public InvalidTargetWordCountException() {
        super("Target word count cannot be negative");
    }
}
