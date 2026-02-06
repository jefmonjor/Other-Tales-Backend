package com.othertales.modules.writing.domain;

import com.othertales.common.domain.ErrorCodes;
import com.othertales.common.domain.ResourceNotFoundException;

import java.util.UUID;

public class ProjectNotFoundException extends ResourceNotFoundException {

    public ProjectNotFoundException(UUID projectId) {
        super("Project not found with id: " + projectId, ErrorCodes.PROJECT_NOT_FOUND);
    }
}
