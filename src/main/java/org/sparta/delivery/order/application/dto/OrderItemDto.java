package org.sparta.delivery.order.application.dto;

import lombok.Builder;

@Builder
public record OrderItemDto(
        String itemCode, // 메뉴 번호
        int quantity // 구매 수량
){}