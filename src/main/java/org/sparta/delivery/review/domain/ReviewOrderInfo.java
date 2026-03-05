package org.sparta.delivery.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.sparta.delivery.store.domain.ReviewOrderItem;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewOrderInfo {
    @Column(length=45, nullable = false)
    private UUID orderId; // 주문번호

    private UUID storeId; // 상점 ID
    private String storeName; // 상점명

    @Column(name="order_items", columnDefinition = "jsonb")
    private List<ReviewOrderItem> items; // 주문상품 목록


}
