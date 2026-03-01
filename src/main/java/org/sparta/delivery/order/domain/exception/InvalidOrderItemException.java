package org.sparta.delivery.order.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

public class InvalidOrderItemException extends BadRequestException {
    public InvalidOrderItemException() {
        super("주문이 불가한 메뉴가 포함되어 있습니다.");
    }
}
