package com.othertales.modules.writing.domain;

import com.othertales.common.domain.ErrorCodes;

import java.util.UUID;

public class ChapterAccessDeniedException extends com.othertales.common.domain.AccessDeniedException {

    public ChapterAccessDeniedException(UUID projectId, UUID userId) {
        super("User " + userId + " does not have access to project " + projectId, ErrorCodes.CHAPTER_ACCESS_DENIED);
    }
}
