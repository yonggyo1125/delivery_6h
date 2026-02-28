package org.sparta.delivery.store.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.BaseEntity;
import org.sparta.delivery.global.domain.Price;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption extends BaseEntity {
    @Column(length=65)
    private String name; // 옵션명

    /*
    [
        {"name": "매운맛", "addPrice": 2000},
        {"name": "순한맛", "addPrice": 0}.
        ...
     ]
    */
    @Column(name="sub_options", columnDefinition = "jsonb")
    private List<ProductSubOption> subOptions; // 하위 옵션


    @AttributeOverrides(
            @AttributeOverride(name="value", column = @Column(name="price"))
    )
    private Price price; // 옵션 추가 금액

    @Builder
    protected ProductOption(String name, int price, List<ProductSubOption> subOptions) {
        this.name = name;
        this.price = new Price(price);
        this.subOptions = subOptions;
    }

    // 옵션 삭제
    protected void remove() {
        deletedAt = LocalDateTime.now();
    }
}
