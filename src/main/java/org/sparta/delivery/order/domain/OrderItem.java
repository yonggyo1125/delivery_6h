package org.sparta.delivery.order.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.Price;
import org.sparta.delivery.order.domain.exception.InvalidOrderItemException;
import org.sparta.delivery.order.domain.service.ProductProvider;

import java.util.List;
import java.util.UUID;

@Embeddable
@ToString @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Embedded
    private ProductInfo item;

    private int quantity;

    @Column(columnDefinition = "jsonb")
    private List<SelectedOption> selectedOptions;

    @AttributeOverrides(
            @AttributeOverride(name="value", column = @Column(name="total_price"))
    )
    private Price totalPrice; // (상품가 + 옵션가) * 수량

    @Builder
    public OrderItem(UUID storeId, String itemCode, ProductProvider productProvider, int quantity, List<SelectedOption> selectedOptions) {

        this.item = productProvider.getProduct(storeId, itemCode);
        if (!item.isOrderable()) { // 주문이 불가한 상품인 경우
            throw new InvalidOrderItemException();
        }

        this.quantity = quantity;

        this.selectedOptions = selectedOptions;

        // 상품별 합계 금액 계산
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        int optionsSum = selectedOptions == null ? 0 : selectedOptions.stream()
                .mapToInt(opt -> opt.getOptionPrice() +
                        opt.getSubOptions().stream().mapToInt(SelectedOption.SelectedSubOption::getAddPrice).sum())
                .sum();

        this.totalPrice = new Price((item.getPrice().getValue() + optionsSum) * quantity);
    }
}
