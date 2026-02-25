package org.sparta.delivery.order.application;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sparta.delivery.order.application.dto.OrderInfoDto;
import org.sparta.delivery.order.application.dto.OrderItemDto;
import org.sparta.delivery.user.test.MockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class CreateOrderServiceTest {
    @Autowired
    CreateOrderService service;

    @Test
    @MockUser(roles={"USER", "OWNER"})
    @DisplayName("주문 등록 테스트")
    @Transactional
    void createOrderTest() {
        Jwt jwt =  (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UUID userId = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");

        List<OrderItemDto> items = List.of(
            new OrderItemDto(UUID.randomUUID(), 1), new OrderItemDto(UUID.randomUUID(), 2)
        );

        OrderInfoDto orderInfo = OrderInfoDto.builder()
                .ordererId(userId)
                .ordererName(name)
                .ordererEmail(email)
                .deliveryAddress("테스트 주소")
                .deliveryAddressDetail("나머지 주소")
                .deliveryMemo("배송 메모")
                .build();

        UUID orderId = service.create(orderInfo, items);
        log.info("orderId: {}", orderId);

        assertNotNull(orderId);
    }
}
