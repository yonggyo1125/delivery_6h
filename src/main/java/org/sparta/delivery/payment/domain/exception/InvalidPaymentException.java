package org.sparta.delivery.payment.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

public class InvalidPaymentException extends BadRequestException {
    public InvalidPaymentException(String message) {
        super(message);
    }

    public InvalidPaymentException() {
        this("유효하지 않은 결제입니다.");
    }
}
