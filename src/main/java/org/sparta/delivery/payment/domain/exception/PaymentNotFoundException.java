package org.sparta.delivery.payment.domain.exception;

import org.sparta.delivery.global.domain.exception.NotFoundException;

public class PaymentNotFoundException extends NotFoundException {
    public PaymentNotFoundException() {
        super("결제 내역을 찾을 수 없습니다.");
    }
}