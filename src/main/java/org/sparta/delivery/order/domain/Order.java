package org.sparta.delivery.order.domain;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

/**
 * 1. 주문상품은 최소 1개이상 주문해야 함
 * 2. 필수 항목
 * 3. 주문 취소는 주문 접수후 5분 이내 가능
 *      - 입금 확인 전: 주문 취소
 *      - 입금 확인 후: 환불
 *  4. 배달 중 상태는 반드시 입금 확인 후 가능
 *  5. 결제 금액은 주문된 상품 목록에서만 계산된다.
 */
@Entity
public class Order {
    private OrderId id;
    private OrderStatus status;

    private Orderer orderer; // 주문자

    private DeliveryInfo deliveryInfo;

    private LocalDateTime createdAt;

    // 주문 취소
    public void cancel() {
        if (!createdAt.isAfter(LocalDateTime.now().minusMinutes(5L))) {
            return;
        }

        if (status == OrderStatus.ORDER_ACCEPT) {
            status = OrderStatus.ORDER_CANCEL;
        } else if (status == OrderStatus.PAYMENT_CONFIRM){
            status = OrderStatus.ORDER_REFUND;
        }
    }

    // 배송 상태
    public void startDelivery() {

    }
}
