package org.sparta.delivery.order.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.OwnerCheck;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.order.application.dto.OrderServiceDto;
import org.sparta.delivery.order.domain.Order;
import org.sparta.delivery.order.domain.OrderId;
import org.sparta.delivery.order.domain.OrderRepository;
import org.sparta.delivery.order.domain.exception.OrderNotFoundException;
import org.sparta.delivery.order.domain.service.OrderCheck;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangeOrderService {
    private final OrderRepository orderRepository;
    private final RoleCheck roleCheck;
    private final OwnerCheck ownerCheck;
    private final OrderCheck orderCheck;

    // 배송 정보 변경
    @Transactional
    public void changeDeliveryInfo(OrderServiceDto.ChangeDelivery dto) {
        Order order = orderRepository.findById(OrderId.of(dto.getOrderId()))
                .orElseThrow(OrderNotFoundException::new);

        order.changeDeliveryInfo(
                dto.getAddress(),
                dto.getAddressDetail(),
                dto.getMemo(),
                roleCheck,
                ownerCheck,
                orderCheck
        );
    }

    // 주문 취소 처리
    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(OrderId.of(orderId))
                .orElseThrow(OrderNotFoundException::new);

        order.cancel(roleCheck, ownerCheck, orderCheck);
    }

    // 배송 시작 처리
    @Transactional
    public void startDelivery(UUID orderId) {
        Order order = orderRepository.findById(OrderId.of(orderId))
                .orElseThrow(OrderNotFoundException::new);

        // 엔티티 내부에서 입금 확인 여부 및 권한을 체크한 뒤 상태를 변경합니다.
        order.delivery(roleCheck, ownerCheck, orderCheck);
    }
}
