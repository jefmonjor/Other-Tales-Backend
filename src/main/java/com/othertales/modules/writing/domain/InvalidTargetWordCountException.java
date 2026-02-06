package com.othertales.modules.writing.domain;

import com.othertales.common.domain.ErrorCodes;
import com.othertales.common.domain.ValidationException;

public class InvalidTargetWordCountException extends ValidationException {

    public InvalidTargetWordCountException() {
        super("Target word count must be positive", ErrorCodes.PROJECT_INVALID_WORD_COUNT);
    }
}
