package org.sparta.delivery.payment.infrastructure.api;

import org.sparta.delivery.payment.domain.PaymentId;
import org.sparta.delivery.payment.domain.service.CancelPayment;
import org.springframework.stereotype.Component;

@Component
public class TossCancelPayment implements CancelPayment {
    @Override
    public void cancel(PaymentId id, String key) {

    }
}
