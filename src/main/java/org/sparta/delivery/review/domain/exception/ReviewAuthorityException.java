package org.sparta.delivery.review.domain.exception;

import org.sparta.delivery.global.domain.exception.UnAuthorizedException;

import java.util.UUID;

public class ReviewAuthorityException extends UnAuthorizedException {
    public ReviewAuthorityException() {
        super("해당 리뷰를 작성하거나 수정할 권한이 없습니다.");
    }

    // 좀 더 상세한 메시지가 필요한 경우
    public ReviewAuthorityException(UUID orderId) {
        super("해당 주문에 대한 리뷰 권한이 없습니다. [주문번호: %s]".formatted(orderId));
    }
}
