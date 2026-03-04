package org.sparta.delivery.payment.infrastructure.api;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.payment.domain.PaymentId;
import org.sparta.delivery.payment.domain.service.ApprovePayment;
import org.sparta.delivery.payment.domain.service.ApproveResult;
import org.springframework.stereotype.Component;

// 결제 승인 처리
@Component
@RequiredArgsConstructor
public class TossApprovePayment implements ApprovePayment {
    @Override
    public ApproveResult request(PaymentId paymentId) {
        return null;
    }
}
