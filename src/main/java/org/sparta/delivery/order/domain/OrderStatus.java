package org.sparta.delivery.order.domain;

public enum OrderStatus {
    ORDER_ACCEPT, // 주문접수
    PAYMENT_CONFIRM, // 입금 확인
    PREPARING, // 배달 준비중
    DELIVERY, // 배달중
    DELIVERY_DONE, // 배달 완료
    ORDER_DONE, // 주문처리 완료
    ORDER_CANCEL, // 주문 취소(미입금)
    ORDER_REFUND, // 환불
    EXCHANGE, // 교환
}