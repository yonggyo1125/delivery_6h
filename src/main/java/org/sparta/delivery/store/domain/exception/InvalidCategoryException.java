package org.sparta.delivery.store.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

public class InvalidCategoryException extends BadRequestException {
    public InvalidCategoryException(String message) {
        super(message);
    }
}
