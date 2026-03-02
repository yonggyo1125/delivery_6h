package org.sparta.delivery.store.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.sparta.delivery.global.domain.BaseEntity;
import org.sparta.delivery.global.domain.Price;
import org.sparta.delivery.store.domain.exception.ProductSubOptionDuplicatedException;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption extends BaseEntity {
    @Column(length=65)
    private String name; // 옵션명

    @Column(name="sub_options", columnDefinition = "jsonb")
    private List<ProductSubOption> subOptions; // 하위 옵션


    @AttributeOverrides(
            @AttributeOverride(name="value", column = @Column(name="price"))
    )
    private Price price; // 옵션 추가 금액

    @Builder
    protected ProductOption(String name, int price, List<ProductSubOption> subOptions) {
        // 하위 옵션 중복 체크
        validateDuplicateSubOptions(subOptions);

        this.name = name;
        this.price = new Price(price);
        this.subOptions = subOptions;
    }

    // 하위 옵션명 중복 검증
    private void validateDuplicateSubOptions(List<ProductSubOption> subOptions) {
        if (subOptions == null || subOptions.isEmpty()) return;

        long uniqueCount = subOptions.stream()
                .map(ProductSubOption::name)
                .distinct()
                .count();

        if (uniqueCount != subOptions.size()) {
            throw new ProductSubOptionDuplicatedException(name);
        }
    }

    // 옵션 삭제
    protected void remove() {
        deletedAt = LocalDateTime.now();
    }
}
