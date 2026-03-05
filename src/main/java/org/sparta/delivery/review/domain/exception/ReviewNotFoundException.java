package org.sparta.delivery.review.domain.exception;

import org.sparta.delivery.global.domain.exception.NotFoundException;

public class ReviewNotFoundException extends NotFoundException {
    public ReviewNotFoundException() {
        super("리뷰를 찾을 수 없습니다.");
    }
}
