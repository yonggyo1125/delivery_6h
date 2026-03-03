package org.sparta.delivery.payment.domain.service;

import org.sparta.delivery.payment.domain.PaymentId;

public interface CancelPayment {
    void cancel(PaymentId id, String key);
}
