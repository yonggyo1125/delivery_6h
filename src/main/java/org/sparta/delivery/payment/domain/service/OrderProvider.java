package org.sparta.delivery.payment.domain.service;

import org.sparta.delivery.payment.domain.PaymentOrderInfo;

import java.util.UUID;

public interface OrderProvider {
    PaymentOrderInfo getOrderInfo(UUID orderId);
}
