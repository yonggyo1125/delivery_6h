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
    @Column(length=45, nullable=false)
    private UUID orderId;

    @Column(length=100, nullable = false)
    private String orderName; // 주문상품명

    @Builder
    protected PaymentOrderInfo(UUID orderId, String orderName) {
        this.orderId = orderId;
        this.orderName = orderName;
    }
}
