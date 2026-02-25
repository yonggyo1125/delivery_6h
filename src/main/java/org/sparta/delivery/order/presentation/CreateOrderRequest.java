package org.sparta.delivery.order.presentation;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        String name,
        String email,
        String address,
        String addressDetail,
        String memo,
        List<Item> items

) {
    record Item(
            UUID itemId, // 메뉴 번호
            int quantity// 구매 수량
    ) {}
}

