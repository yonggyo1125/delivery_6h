package org.sparta.delivery.order.domain.service;

import org.sparta.delivery.order.domain.OrderId;
import org.sparta.delivery.order.domain.OrderItem;

import java.util.List;
import java.util.UUID;

public interface OrderCheck {
    /**
     * 주문 가능 여부 체크
     *  1. 매장의 영업 여부 체크: Store::isVisible()
     *  2. 매장의 주문 가능 시간 체크: Store::isOrderable()
     *  3. 상품의 주문 가능 여부 체크: Product::isOrderable()
     *
     * @param items 주문 상품 목록
     * @return
     */
    boolean isOrderable(UUID storeId, List<OrderItem> items); // 주문 가능 여부 체크
    boolean isMyOrder(OrderId orderId); // 주문이 로그인한 회원의 주문인지 체크
}
