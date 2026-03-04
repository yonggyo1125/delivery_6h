package org.sparta.delivery.payment.domain;

import java.time.LocalDateTime;

public record PaymentLog(
        LocalDateTime createdAt,
        String log
) {}
