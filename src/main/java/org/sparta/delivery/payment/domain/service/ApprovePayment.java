package org.sparta.delivery.payment.domain.service;

import org.sparta.delivery.payment.domain.PaymentId;

// 결제 승인 처리
public interface ApprovePayment {
    // 승인 요청 처리
    ApproveResult request(PaymentId paymentId);
}
