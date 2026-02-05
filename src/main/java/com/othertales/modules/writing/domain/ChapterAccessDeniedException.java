package com.othertales.modules.writing.domain;

import java.util.UUID;

public class ChapterAccessDeniedException extends RuntimeException {

    private final UUID projectId;
    private final UUID userId;

    public ChapterAccessDeniedException(UUID projectId, UUID userId) {
        super("User " + userId + " does not have access to project " + projectId);
        this.projectId = projectId;
        this.userId = userId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getUserId() {
        return userId;
    }
}
