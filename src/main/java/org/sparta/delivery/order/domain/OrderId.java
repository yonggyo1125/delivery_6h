package org.sparta.delivery.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@ToString @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderId {
    @Column(length = 45, name="order_id")
    private UUID id;

    public static OrderId of() {
        return OrderId.of(UUID.randomUUID());
    }

    public static OrderId of(UUID id) {
        return new OrderId(id);
    }
}
