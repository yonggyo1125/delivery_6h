package org.sparta.delivery.order.domain.event;

import java.util.UUID;

// 주문 접수 이벤트
public record OrderAcceptEvent(
        UUID orderId
) {}