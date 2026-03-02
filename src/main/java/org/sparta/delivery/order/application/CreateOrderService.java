package org.sparta.delivery.order.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.OwnerCheck;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.global.domain.service.UserDetails;
import org.sparta.delivery.order.application.dto.OrderServiceDto;
import org.sparta.delivery.order.domain.Order;
import org.sparta.delivery.order.domain.OrderItem;
import org.sparta.delivery.order.domain.OrderRepository;
import org.sparta.delivery.order.domain.SelectedOption;
import org.sparta.delivery.order.domain.service.OrderCheck;
import org.sparta.delivery.order.domain.service.ProductProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderService {
    private final OrderRepository orderRepository;
    private final OrderCheck orderCheck;
    private final UserDetails userDetails;
    private final RoleCheck roleCheck;
    private final OwnerCheck ownerCheck;
    private final ProductProvider productProvider;

    @Transactional
    public UUID createOrder(OrderServiceDto.Create dto) {

        // OrderItem + SelectedOption로 변환
        List<OrderItem> orderItems = dto.getItems().stream()
                .map(item -> toOrderItem(dto.getStoreId(), item))
                .toList();

        // Order 엔티티 생성
        Order order = Order.builder()
                .ordererName(dto.getOrdererName())
                .ordererEmail(dto.getOrdererEmail())
                .ordererMobile(dto.getOrdererMobile())
                .storeId(dto.getStoreId())
                .storeName(dto.getStoreName())
                .storeAddress(dto.getStoreAddress())
                .storeTel(dto.getStoreTel())
                .deliveryAddress(dto.getDeliveryAddress())
                .deliveryAddressDetail(dto.getDeliveryAddressDetail())
                .deliveryMemo(dto.getDeliveryMemo())
                .orderItems(orderItems)
                .orderCheck(orderCheck)
                .userDetails(userDetails)
                .build();

        // 주문 접수 처리
        order.orderAccept(roleCheck, ownerCheck, orderCheck);

        return orderRepository.save(order).getId().getId();
    }

    private OrderItem toOrderItem(UUID storeId, OrderServiceDto.Item itemDto) {
        List<SelectedOption> selectedOptions = itemDto.getOptions() == null ? List.of() :
                itemDto.getOptions().stream()
                        .map(opt -> SelectedOption.builder()
                                .optionName(opt.getName())
                                .optionPrice(opt.getPrice())
                                .subOptions(opt.getSubOptions().stream()
                                        .map(sub -> SelectedOption.SelectedSubOption.builder()
                                                .name(sub.getName())
                                                .addPrice(sub.getPrice())
                                                .build())
                                        .toList())
                                .build())
                        .toList();

        return OrderItem.builder()
                .storeId(storeId)
                .productProvider(productProvider)
                .itemCode(itemDto.getItemCode())
                .quantity(itemDto.getQuantity())
                .selectedOptions(selectedOptions)
                .build();
    }
}