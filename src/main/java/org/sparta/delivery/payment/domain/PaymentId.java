package org.sparta.delivery.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentId {
    @Column(length = 45, name="payment_id")
    private UUID id;

    public static PaymentId of() {
        return PaymentId.of(UUID.randomUUID());
    }

    public static PaymentId of(UUID id) {
        return new PaymentId(id);
    }
}
