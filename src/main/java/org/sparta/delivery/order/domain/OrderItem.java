package org.sparta.delivery.order.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.Price;

import java.util.List;

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

    @Column(columnDefinition = "jsonb")
    private List<SelectedOption> selectedOptions;

    @AttributeOverrides(
            @AttributeOverride(name="value", column = @Column(name="total_price"))
    )
    private Price totalPrice; // (상품가 + 옵션가) * 수량

    @Builder
    public OrderItem(String itemCode, String itemName, int price, int quantity, List<SelectedOption> selectedOptions) {
        item = new ProductInfo(itemCode, itemName);
        this.price = new Price(price);
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

        this.totalPrice = new Price((this.price.getValue() + optionsSum) * quantity);
    }
}
