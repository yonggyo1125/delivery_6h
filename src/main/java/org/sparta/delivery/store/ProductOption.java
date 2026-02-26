package org.sparta.delivery.store;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.Price;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {
    @Column(length=65)
    private String name; // 옵션명

    @AttributeOverrides(
            @AttributeOverride(name="value", column = @Column(name="price"))
    )
    private Price price; // 옵션 추가 금액

    @Builder
    protected ProductOption(String name, int price) {
        this.name = name;
        this.price = new Price(price);
    }
}
