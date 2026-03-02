package org.sparta.delivery.order.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.Price;

@Embeddable
@ToString @Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
public class ProductInfo {
    @Column(length=45, name="item_code", nullable = false)
    private String code; // 상품 ProductCode

    @Column(length=100, name="item_name", nullable = false)
    private String name; // 상품명

    @AttributeOverrides(
            @AttributeOverride(name="value", column = @Column(name="price"))
    )
    private Price price;

    @Transient
    private boolean orderable;

    @Builder
    protected ProductInfo(String code, String name, int price, boolean orderable) {
        this.code = code;
        this.name = name;
        this.price = new Price(price);
        this.orderable = orderable;
    }
}
