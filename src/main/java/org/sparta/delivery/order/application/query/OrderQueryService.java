package org.sparta.delivery.order.application.query;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.order.domain.Order;
import org.sparta.delivery.order.domain.OrderId;
import org.sparta.delivery.order.domain.exception.OrderNotFoundException;
import org.sparta.delivery.order.domain.query.OrderQueryDto;
import org.sparta.delivery.order.domain.query.OrderQueryRepository;
import org.sparta.delivery.order.presentation.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderQueryRepository orderQueryRepository;

    public OrderResponseDto.OrderDetail getOrderDetail(UUID orderId) {
        org.sparta.delivery.order.domain.Order order = orderQueryRepository.findById(OrderId.of(orderId))
                .orElseThrow(OrderNotFoundException::new);

        return toDetailResponse(order);
    }

    public Page<OrderResponseDto.Order> getOrders(OrderQueryDto.Search search, Pageable pageable) {
        return orderQueryRepository.findAll(search, pageable)
                .map(this::toOrderResponse);
    }

    public Page<OrderResponseDto.Order> getStoreOrders(UUID storeId, OrderQueryDto.Search search, Pageable pageable) {
        return orderQueryRepository.findAllByStore(storeId, search, pageable)
                .map(this::toOrderResponse);
    }

    public Page<OrderResponseDto.Order> getUserOrders(UUID userId, OrderQueryDto.Search search, Pageable pageable) {
        return orderQueryRepository.findAllByUser(userId, search, pageable)
                .map(this::toOrderResponse);
    }

    private OrderResponseDto.Order toOrderResponse(Order order) {
        return OrderResponseDto.Order.builder()
                .orderId(order.getId().getId())
                .storeName(order.getStoreInfo().getStoreName())
                .totalOrderPrice(order.getTotalOrderPrice().getValue())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderResponseDto.OrderDetail toDetailResponse(Order order) {
        return OrderResponseDto.OrderDetail.builder()
                .orderId(order.getId().getId())
                .storeName(order.getStoreInfo().getStoreName())
                .ordererName(order.getOrderer().getName())
                .deliveryAddress(order.getDeliveryInfo().getAddress())
                .deliveryMemo(order.getDeliveryInfo().getMemo())
                .totalOrderPrice(order.getTotalOrderPrice().getValue())
                .status(order.getStatus())
                .items(order.getOrderItems().stream().map(item ->
                        OrderResponseDto.OrderItem.builder()
                                .itemName(item.getItem().getName())
                                .quantity(item.getQuantity())
                                .price(item.getItem().getPrice().getValue())
                                .totalPrice(item.getTotalPrice().getValue())
                                .selectedOptions(item.getSelectedOptions().stream().map(opt ->
                                        OrderResponseDto.Option.builder()
                                                .optionName(opt.getOptionName())
                                                .optionPrice(opt.getOptionPrice())
                                                .subOptions(opt.getSubOptions().stream().map(sub ->
                                                        OrderResponseDto.SubOption.builder()
                                                                .name(sub.getName())
                                                                .addPrice(sub.getAddPrice())
                                                                .build()
                                                ).toList())
                                                .build()
                                ).toList())
                                .build()
                ).toList())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
