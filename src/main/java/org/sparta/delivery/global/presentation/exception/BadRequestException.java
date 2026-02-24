package org.sparta.delivery.global.presentation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

@Getter
public class BadRequestException extends HttpStatusCodeException {

    private String field;
    private String message;

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(String field, String message) {
        this(message);
        this.message = message;
        this.field = field;
    }
}