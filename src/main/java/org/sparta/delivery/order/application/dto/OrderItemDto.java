package org.sparta.delivery.order.application.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderItemDto(
        UUID itemId, // 메뉴 번호
        int quantity // 구매 수량
){}