package org.sparta.delivery.order.presentation;

import java.util.List;

public record CreateOrderRequest(
        String name,
        String email,
        String address,
        String addressDetail,
        String memo,
        List<Item> items

) {
    record Item(
            String itemCode, // 메뉴 번호
            int quantity// 구매 수량
    ) {}
}

