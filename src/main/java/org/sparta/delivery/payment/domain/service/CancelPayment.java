package org.sparta.delivery.payment.domain.service;

import org.sparta.delivery.payment.domain.PaymentId;

public interface CancelPayment {
    CancelResult cancel(PaymentId paymentId, String paymentKey, String cancelReason);
}
