package org.sparta.delivery.order.domain.event;

import java.util.UUID;

// 주문 환불 단계로 변경시 발생 이벤트
public record OrderRefundEvent(
        UUID orderId
) {}
