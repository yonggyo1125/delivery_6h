package org.sparta.delivery.store.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.sparta.delivery.global.domain.BaseUserEntity;
import org.sparta.delivery.global.domain.Price;

import java.time.LocalDateTime;
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
 * 상품이 삭제되지 않고 판매중(SALE)일때만 주문 가능
 *
 */
@Getter
@ToString
@Entity
@Table(name="P_PRODUCT")
@SQLRestriction("deleted_at IS NULL")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseUserEntity {

    @EmbeddedId
    private ProductId id;

    @Version
    private int version; // 낙관적 Lock

    @Column(length=30, unique = true, nullable = false)
    private String productCode; // 상품 관리 코드

    private UUID category;

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
    @SQLRestriction("deleted_at IS NULL")
    @OrderColumn(name="option_idx")
    private List<ProductOption> options;

    @Builder
    protected Product(UUID categoryId, String productCode, String name, int price, List<ProductOption> options) {
        this.category = categoryId;
        this.productCode = productCode;
        this.name = name;
        this.price = new Price(price);
        this.status = ProductStatus.READY;

        this.options = new ArrayList<>();
        if (options != null) {
            this.options.addAll(options);
        }
    }

    // 상품 삭제 (Soft Delete)
    public void remove() {

        deletedAt = LocalDateTime.now();

        // 옵션 삭제
        options.forEach(ProductOption::remove);
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

    // 옵션 여러개 삭제(Soft Delete)
    public void removeOptions(List<Integer> indexes) {
        if (this.options == null || indexes == null) return;

        IntStream.range(0, this.options.size())
                .filter(indexes::contains)
                .mapToObj(this.options::get)
                .forEach(ProductOption::remove);
    }

    // 옵션 한개 삭제
    public void removeOption(int index) {
        if (options != null && index >= 0 && index < options.size()) {
            removeOptions(List.of(index));
        }
    }

    // 옵션 비우기
    public void truncateOption() {
        if (options != null) {
            options.clear();
        }

    }

    // 옵션 교체
    public void replaceOption(List<ProductOption> options) {
        truncateOption();
        createOptions(options);
    }

    // 주문 가능 여부
    public boolean isOrderable() {
        return status == ProductStatus.SALE && getDeletedAt() == null;
    }


    // 상태 변경 (매장을 통해서만 상태 변경 가능)
    protected void changeStatus(ProductStatus status) {
        this.status = status;
    }

    // 상품 노출 가능 여부
    public boolean isVisible() {
        return getDeletedAt() == null && status != ProductStatus.READY;
    }
}
