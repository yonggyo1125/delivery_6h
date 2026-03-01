package org.sparta.delivery.global.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Objects;

@Getter
public class CustomException extends HttpStatusCodeException {

    protected String field;
    protected String message;

    public CustomException(String message, HttpStatus status) {
            super(Objects.requireNonNullElse(status, HttpStatus.INTERNAL_SERVER_ERROR), message);
        }

    public CustomException(String field, String message, HttpStatus status) {
            this(message, status);
            this.message = message;
            this.field = field;
        }
}
