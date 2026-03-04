package org.sparta.delivery.payment.domain.service;

import org.sparta.delivery.payment.domain.PaymentId;

public interface ApprovePayment {
    // 승인 요청 처리
    ApproveResult request(PaymentId paymentId);
}
