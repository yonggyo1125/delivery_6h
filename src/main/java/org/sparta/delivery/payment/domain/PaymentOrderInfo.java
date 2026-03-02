package org.sparta.delivery.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentOrderInfo {

    @Column(length = 45, nullable = false, updatable = false)
    private UUID orderId;

    @Column(length = 100, nullable = false, updatable = false)
    private String orderName;

    @Builder
    protected PaymentOrderInfo(UUID orderId, String orderName) {
        this.orderId = orderId;
        this.orderName = orderName;
    }
}