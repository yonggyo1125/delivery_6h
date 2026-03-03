package org.sparta.delivery.payment.domain.service;

public record CancelResult(
        boolean success,
        String reason,
        String paymentLog
) {}
