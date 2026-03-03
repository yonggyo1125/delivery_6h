package org.sparta.delivery.order.domain.event;

import java.util.UUID;

public record OrderPaymentConfirmedEvent(
        UUID orderId
) {}
