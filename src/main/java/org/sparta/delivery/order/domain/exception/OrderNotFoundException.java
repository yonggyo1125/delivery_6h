package org.sparta.delivery.order.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class OrderNotFoundException extends HttpStatusCodeException {
    public OrderNotFoundException() {
        super(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.");
    }
}