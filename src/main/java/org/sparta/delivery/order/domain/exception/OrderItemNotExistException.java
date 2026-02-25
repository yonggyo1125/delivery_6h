package org.sparta.delivery.order.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class OrderItemNotExistException extends HttpStatusCodeException {
    public OrderItemNotExistException() {
        super(HttpStatus.NOT_FOUND, "주문상품이 누락되었습니다.");
    }
}