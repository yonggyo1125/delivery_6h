package org.sparta.delivery.order.application.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderInfoDto(
        UUID ordererId,
        String ordererName,
        String ordererEmail,
        String deliveryAddress,
        String deliveryAddressDetail,
        String deliveryMemo
) {}