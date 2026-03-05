package org.sparta.delivery.review.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

import java.util.UUID;

public class InvalidOrderStateForReviewException extends BadRequestException {
    public InvalidOrderStateForReviewException(UUID orderId) {
        super("리뷰 작성이 가능한 주문을 찾을 수 없습니다. [주문번호: %s]".formatted(orderId));
    }
}
