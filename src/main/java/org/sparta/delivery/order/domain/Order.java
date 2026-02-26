package org.sparta.delivery.order.domain;

/*
1. 회원의 권한이 있어야 주문이 가능
2. 상품은 1개 이상 있어야 주문 가능
3. 주문 취소는 주문 5분 이내에만 가능
..
 */
public class Order {

    private OrderId id;

    private Orderer orderer;

    private DeliveryInfo deliveryInfo;


    public void cancel() {

    }
}
