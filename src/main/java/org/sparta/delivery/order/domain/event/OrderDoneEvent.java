package org.sparta.delivery.order.domain.event;

import java.util.UUID;

public record OrderDoneEvent(UUID orderId) {}
