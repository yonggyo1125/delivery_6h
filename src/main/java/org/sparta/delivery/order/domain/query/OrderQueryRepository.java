package org.sparta.delivery.order.domain.query;

import org.sparta.delivery.order.domain.Order;
import org.sparta.delivery.order.domain.OrderId;

import java.awt.print.Pageable;
import java.util.Optional;
import java.util.UUID;

/**
 * 1. 주문조회(findById)의 경우 자신의 주문건(사용자), 또는 매장 점주의 주문건으로 한정, 관리자의 경우는 제한 없음
 */
public interface OrderQueryRepository {
    // 주문 조회
    Optional<Order> findById(OrderId orderId);


    // 매장별 주문목록 조회
    Optional<Order> findAllByStore(UUID storeId, OrderQueryDto.Search search, Pageable pageable);

    // 사용자별 주문목록 조회
    Optional<Order> findAllByUser(UUID userId, OrderQueryDto.Search search, Pageable pageable);

    // 주문 목록 조회
    Optional<Order> findAll(OrderQueryDto.Search search, Pageable pageable);
}
