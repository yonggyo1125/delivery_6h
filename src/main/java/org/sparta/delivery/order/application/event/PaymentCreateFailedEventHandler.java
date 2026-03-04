package org.sparta.delivery.order.application.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.order.domain.Order;
import org.sparta.delivery.order.domain.OrderId;
import org.sparta.delivery.order.domain.OrderRepository;
import org.sparta.delivery.order.domain.exception.OrderNotFoundException;
import org.sparta.delivery.payment.domain.event.PaymentCreateFailedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCreateFailedEventHandler {

    private final OrderRepository orderRepository;

     // 결제 등록 재시도 후 최종 실패한 경우 주문 취소 처리
    @Async
    @EventListener
    @Retryable(
            retryFor = { Exception.class },
            noRetryFor = { OrderNotFoundException.class }, // 주문서가 없다면 재시도는 무의미
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2.0)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(PaymentCreateFailedEvent event) {
        UUID orderId = event.orderId();
        Order order = orderRepository.findById(OrderId.of(orderId)).orElseThrow(OrderNotFoundException::new);

        order.systemCancel();
    }

    @Recover
    public void failCompletion(Exception e, PaymentCreateFailedEvent event) {
        log.error("주문 취소 최종 실패. 사유: {}. 처리를 종료 합니다. 주문ID: {}",
                e.getMessage(), event.orderId());
    }
}
