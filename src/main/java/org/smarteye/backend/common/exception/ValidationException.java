package org.smarteye.backend.common.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

/** 422 Unprocessable Entity для нарушений бизнес-валидации. */
public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
    public ValidationException(String message, Map<String, Object> details) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message, details);
    }
}
