package org.sparta.delivery.order.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.Price;

import java.util.UUID;

@Embeddable
@ToString @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Embedded
    private ProductInfo item;

    @AttributeOverrides(
            @AttributeOverride(name="value", column = @Column(name="price"))
    )
    private Price price;

    private int quantity;

    @AttributeOverrides(
            @AttributeOverride(name="value", column = @Column(name="total_price"))
    )
    private Price totalPrice;

    @Builder
    public OrderItem(UUID itemId, String itemName, int price, int quantity) {
        item = new ProductInfo(itemId, itemName);
        this.price = new Price(price);
        this.quantity = quantity;

        // 상품별 합계 금액 계산
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        this.totalPrice = price.multiply(quantity);
    }
}
