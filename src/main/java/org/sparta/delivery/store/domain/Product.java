package org.sparta.delivery.store.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.BaseUserEntity;
import org.sparta.delivery.global.domain.Price;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * 상품 등록 및 수정시 분류는 매장에 등록된 분류에 있는지 체크
 * 상품 상태는 준비중, 판매중, 품절이 있으며, 준비중 상태에서는 상품은 미노출, 품절은 노출되지만 주문에 제한이 있음
 * 상품 등록시 기본값은 상품 준비중
 * 옵션은 상품을 통해서 등록, 수정, 삭제 가능
 */
@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseUserEntity {

    @EmbeddedId
    private ProductId id;

    @Version
    private int version;

    @Column(length=30, unique = true, nullable = false)
    private String productCode; // 상품 관리 코드

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
    private Price price; // 상품가

    // 옵션 - 1:N 관계
    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name="P_PRODUCT_OPTION", joinColumns = {
            @JoinColumn(name="store_id"),
            @JoinColumn(name="product_idx")
    })
    @OrderColumn(name="option_idx")
    private List<ProductOption> options;

    @Builder
    protected Product(UUID categoryId, String productCode, String name, int price, List<ProductOption> options) {
        this.category = new StoreCategory(categoryId);
        this.productCode = productCode;
        this.name = name;
        this.price = new Price(price);
        this.status = ProductStatus.READY;

        this.options = new ArrayList<>();
        if (options != null) {
            this.options.addAll(options);
        }
    }

    // 옵션 등록
    public void createOptions(List<ProductOption> newOptions) {
        if (newOptions == null || newOptions.isEmpty()) return;
        this.options = Objects.requireNonNullElseGet(this.options, ArrayList::new);
        this.options.addAll(newOptions);
    }

    // 옵션 한개 등록
    public void createOption(String name, int price) {
        createOption(name, price, null);
    }

    public void createOption(String name, int price, List<ProductSubOption> subOptions) {
        options = Objects.requireNonNullElseGet(options, ArrayList::new);
        options.add(new ProductOption(name, price, subOptions));
    }

    // 옵션 여러개 삭제
    public void removeOptions(List<Integer> indexes) {
        if (this.options == null || indexes == null) return;

        List<ProductOption> remaining = IntStream.range(0, this.options.size())
                .filter(i -> !indexes.contains(i))
                .mapToObj(this.options::get)
                .toList();

        this.options.clear();
        this.options.addAll(remaining);
    }

    // 옵션 한개 삭제
    public void removeOption(int index) {
        if (options != null && index >= 0 && index < options.size()) {
            removeOptions(List.of(index));
        }
    }

    // 옵션 비우기
    public void truncate() {
        if (options != null) {
            options.clear();
        }

    }

    // 옵션 교체
    public void replace(List<ProductOption> options) {
        truncate();
        createOptions(options);
    }
}
