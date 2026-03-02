package org.sparta.delivery.global.domain.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {

    public NotFoundException(String message) {
        super( message, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String field, String message) {
        super(field, message, HttpStatus.NOT_FOUND);

    }
}
