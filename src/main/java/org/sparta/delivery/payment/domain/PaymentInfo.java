package org.sparta.delivery.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentInfo {
    @Column(length=45, nullable=false, name="payment_key")
    private String key;

    @Column(length=30, nullable = false, name="payment_status")
    private PaymentStatus status;

    public LocalDateTime requestedAt; // 결제 요청일시
    private LocalDateTime approvedAt; // 결제 승인일시

    @Builder
    protected PaymentInfo(String key, PaymentStatus status, LocalDateTime requestedAt, LocalDateTime approvedAt) {
        this.key = key;
        this.status = status;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }
}
