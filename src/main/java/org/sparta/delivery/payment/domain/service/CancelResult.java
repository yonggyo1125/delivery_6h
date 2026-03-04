package org.sparta.delivery.payment.domain.service;

import lombok.Builder;

@Builder
public record CancelResult(
        boolean success,
        String reason,
        String paymentLog
) {}
