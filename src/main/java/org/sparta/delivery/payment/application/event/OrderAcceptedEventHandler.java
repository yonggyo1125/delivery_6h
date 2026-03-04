package org.sparta.delivery.payment.application.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.global.infrastructure.event.Events;
import org.sparta.delivery.order.domain.event.OrderAcceptedEvent;
import org.sparta.delivery.payment.application.PaymentService;
import org.sparta.delivery.payment.domain.event.PaymentCreateFailedEvent;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// 주문 접수 후 결제 등록처리 핸들러
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAcceptedEventHandler {
    private final PaymentService paymentService;

    @Async
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2.0)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderAcceptedEvent event) {
        // 주문이 접수되면 결제 등록(READY) 처리
        paymentService.create(event.orderId());
    }

    // 모든 재시도가 실패하면 주문서의 상태를 주문 취소(ORDER_CANCEL)로 변경한다.
    @Recover
    public void recover(Exception e, OrderAcceptedEvent event) {
        log.error("결제등록 최종 실패. 사유: {}. 주문 취소를 진행합니다. 주문ID: {}",
                e.getMessage(), event.orderId());

        // 주문취소를 위해 이벤트 발행 (주문쪽에서 이벤트 수신 후 주문 취소 처리)
        Events.trigger(new PaymentCreateFailedEvent(event.orderId()));
    }
}
