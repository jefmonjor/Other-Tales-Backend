package com.othertales.modules.writing.domain;

import java.util.UUID;

public class ChapterNotFoundException extends RuntimeException {

    private final UUID chapterId;

    public ChapterNotFoundException(UUID chapterId) {
        super("Chapter not found: " + chapterId);
        this.chapterId = chapterId;
    }

    public UUID getChapterId() {
        return chapterId;
    }
}
