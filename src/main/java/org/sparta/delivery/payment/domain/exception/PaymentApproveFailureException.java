package org.sparta.delivery.payment.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

public class PaymentApproveFailureException extends BadRequestException {
    public PaymentApproveFailureException(String reason) {
        super("PG 결제 승인 처리에 실패했습니다:" + reason);
    }
}
