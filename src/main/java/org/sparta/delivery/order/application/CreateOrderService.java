package org.sparta.delivery.order.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.OwnerCheck;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.order.application.dto.OrderInfoDto;
import org.sparta.delivery.order.application.dto.OrderItemDto;
import org.sparta.delivery.order.domain.Order;
import org.sparta.delivery.order.domain.OrderItem;
import org.sparta.delivery.order.domain.OrderRepository;
import org.sparta.delivery.order.domain.service.OrderCheck;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderService {
    private final OrderRepository orderRepository;
    private final RoleCheck roleCheck;
    private final OwnerCheck ownerCheck;
    private final OrderCheck orderCheck;

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public UUID create(OrderInfoDto orderInfo, List<OrderItemDto> items) {

        List<OrderItem> orderItems = toOrderItem(items);

        Order order = Order.builder()
                .orderCheck(orderCheck)
                .ordererName(orderInfo.ordererName())
                .ordererEmail(orderInfo.ordererEmail())
                .deliveryAddress(orderInfo.deliveryAddress())
                .deliveryAddressDetail(orderInfo.deliveryAddressDetail())
                .deliveryMemo(orderInfo.deliveryMemo())
                .orderItems(orderItems)
                .build();

        // 주문 접수 상태 변경 - 도메인 로직
        order.orderAccept(roleCheck, ownerCheck, orderCheck);

        orderRepository.save(order);

        return order.getId().getId();
    }

    private List<OrderItem> toOrderItem(List<OrderItemDto> items) {
        return items == null ? null : items.stream()
                .map(item -> OrderItem.builder()
                        .itemCode(item.itemCode())
                        .itemName("구현예정")
                        .price(1000)
                        .quantity(item.quantity())
                        .build())
                .toList();
    }
}