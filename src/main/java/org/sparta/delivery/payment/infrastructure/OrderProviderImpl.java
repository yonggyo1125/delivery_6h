package org.sparta.delivery.payment.infrastructure;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.order.domain.Order;
import org.sparta.delivery.order.domain.OrderId;
import org.sparta.delivery.order.domain.OrderItem;
import org.sparta.delivery.order.domain.exception.OrderItemNotExistException;
import org.sparta.delivery.order.domain.exception.OrderNotFoundException;
import org.sparta.delivery.order.domain.query.OrderQueryRepository;
import org.sparta.delivery.payment.domain.PaymentOrderInfo;
import org.sparta.delivery.payment.domain.service.OrderProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderProviderImpl implements OrderProvider {
    private final OrderQueryRepository orderQueryRepository;

    @Override
    public PaymentOrderInfo getOrderInfo(UUID orderId) {
        Order order = orderQueryRepository.findById(OrderId.of(orderId)).orElseThrow(OrderNotFoundException::new);

        List<OrderItem> items = order.getOrderItems();
        if (items == null || items.isEmpty()) {
            throw new OrderItemNotExistException();
        }

        String firstItemName = items.getFirst().getItem().getName();
        String orderName = (items.size() > 1)
                ? "%s 외 %d건".formatted(firstItemName, items.size() - 1)
                : firstItemName;

        // 결제 상품명의 최대 길이가 100자 이내
        if (orderName.length() > 100) {
            orderName = orderName.substring(0, 97) + "...";
        }

        return PaymentOrderInfo.builder()
                .orderId(orderId)
                .orderName(orderName)
                .amount(order.getTotalOrderPrice().getValue())
                .build();

    }
}
