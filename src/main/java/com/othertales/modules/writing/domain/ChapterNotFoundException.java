package com.othertales.modules.writing.domain;

import com.othertales.common.domain.ErrorCodes;
import com.othertales.common.domain.ResourceNotFoundException;

import java.util.UUID;

public class ChapterNotFoundException extends ResourceNotFoundException {

    public ChapterNotFoundException(UUID chapterId) {
        super("Chapter not found: " + chapterId, ErrorCodes.CHAPTER_NOT_FOUND);
    }
}
