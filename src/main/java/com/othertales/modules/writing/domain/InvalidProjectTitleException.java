package com.othertales.modules.writing.domain;

import com.othertales.common.domain.ErrorCodes;
import com.othertales.common.domain.ValidationException;

public class InvalidProjectTitleException extends ValidationException {

    public InvalidProjectTitleException() {
        super("Project title cannot be null or empty", ErrorCodes.PROJECT_INVALID_TITLE);
    }
}
