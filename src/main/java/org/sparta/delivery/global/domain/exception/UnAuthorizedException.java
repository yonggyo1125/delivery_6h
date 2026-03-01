package org.sparta.delivery.global.domain.exception;

import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends CustomException {
    public UnAuthorizedException() {
        this("접근 권한이 없습니다.");
    }
    public UnAuthorizedException(String message) {
        super( message, HttpStatus.UNAUTHORIZED);
    }

    public UnAuthorizedException(String field, String message) {
        super(field, message, HttpStatus.UNAUTHORIZED);

    }
}
