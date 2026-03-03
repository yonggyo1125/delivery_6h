package org.sparta.delivery.payment.infrastructure.api;

import org.sparta.delivery.payment.domain.PaymentId;
import org.sparta.delivery.payment.domain.service.CancelPayment;
import org.sparta.delivery.payment.domain.service.CancelResult;
import org.springframework.stereotype.Component;

@Component
public class TossCancelPayment implements CancelPayment {
    @Override
    public CancelResult cancel(PaymentId id, String key) {
        return null;
    }
}
