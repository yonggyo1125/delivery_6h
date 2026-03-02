package org.sparta.delivery.order.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderServiceDto {
    @Getter
    @Builder
    public static class Create {

    }
}
