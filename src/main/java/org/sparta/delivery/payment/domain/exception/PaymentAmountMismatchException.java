package org.sparta.delivery.payment.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

public class PaymentAmountMismatchException extends BadRequestException {
    public PaymentAmountMismatchException(int expected, int actual) {
        super("결제 금액이 일치하지 않습니다. (요청 금액: %d, 실결제 금액: %d)".formatted(expected, actual));
    }
}
