package org.sparta.delivery.store;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.BaseUserEntity;
import org.sparta.delivery.global.domain.Price;

import java.util.List;
import java.util.UUID;

/**
 * 상품 등록 및 수정시 분류는 매장에 등록된 분류에 있는지 체크
 * 상품 상태는 준비중, 판매중, 품절이 있으며, 준비중 상태에서는 상품은 미노출, 품절은 노출되지만 주문에 제한이 있음
 * 상품 등록시 기본값은 상품 준비중
 */
@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseUserEntity {
    @Embedded
    private StoreCategory category;

    @Column(length=20)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(length=65)
    private String name;

    @AttributeOverrides(
            @AttributeOverride(name="value", column = @Column(name="price"))
    )
    private Price price;

    // 옵션 - 1:N 관계
    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name="P_PRODUCT_OPTION", joinColumns = {
            @JoinColumn(name="store_id"),
            @JoinColumn(name="product_idx")
    })
    @OrderColumn(name="option_idx")
    private List<ProductOption> options;

    @Builder
    protected Product(UUID categoryId, String name, int price) {
        this.category = new StoreCategory(categoryId);
        this.name = name;
        this.price = new Price(price);
        this.status = ProductStatus.READY;
    }
}
