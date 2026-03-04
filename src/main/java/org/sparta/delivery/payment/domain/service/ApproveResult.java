package org.sparta.delivery.payment.domain.service;

import lombok.Builder;
import org.sparta.delivery.payment.domain.PaymentStatus;

import java.time.LocalDateTime;

// 승인 결과 반환 값
@Builder
public record ApproveResult(
        boolean success,
        String reason,
        String key,
        PaymentStatus status,
        LocalDateTime approvedAt,
        String paymentLog,
        int approvedAmount
) {}
