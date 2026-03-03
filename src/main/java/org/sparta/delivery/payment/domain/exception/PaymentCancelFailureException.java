package org.sparta.delivery.payment.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

public class PaymentCancelFailureException extends BadRequestException {
    public PaymentCancelFailureException(String reason) {
        super("PG 결제 취소 처리에 실패했습니다:" + reason);
    }
}
