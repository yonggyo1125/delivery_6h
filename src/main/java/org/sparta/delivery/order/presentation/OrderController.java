package org.sparta.delivery.order.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.order.application.CreateOrderService;
import org.sparta.delivery.order.application.dto.OrderInfoDto;
import org.sparta.delivery.order.application.dto.OrderItemDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private CreateOrderService createService;

    // 주문 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest req, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        String name = jwt.getClaim("family_name") + (String)jwt.getClaim("given_name");
        String email = jwt.getClaim("email");

        OrderInfoDto orderInfo = OrderInfoDto
                .builder()
                .ordererId(userId)
                .ordererEmail(StringUtils.hasText(req.email()) ? req.email() : email)
                .ordererName(StringUtils.hasText(req.name()) ? req.name() : name)
                .deliveryMemo(req.memo())
                .deliveryAddress(req.address())
                .deliveryAddressDetail(req.addressDetail())
                .build();

        List<OrderItemDto> items = req.items() == null ? null : req.items().stream()
                .map(item -> new OrderItemDto(item.itemId(), item.quantity()))
                .toList();

        UUID orderId = createService.create(orderInfo, items);

        return new CreateOrderResponse(orderId);
    }
}
