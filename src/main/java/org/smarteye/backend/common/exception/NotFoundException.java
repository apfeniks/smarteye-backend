package org.smarteye.backend.common.exception;

import org.springframework.http.HttpStatus;

/** 404 Not Found для доменных объектов/ресурсов. */
public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
