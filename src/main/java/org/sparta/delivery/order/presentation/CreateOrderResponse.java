package org.sparta.delivery.order.presentation;

import java.util.UUID;

public record CreateOrderResponse(
        UUID orderId
) {}