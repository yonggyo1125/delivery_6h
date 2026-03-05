package org.sparta.delivery.review.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

public class ReviewRateOutOfRangeException extends BadRequestException {
    public ReviewRateOutOfRangeException(int rate) {
        super("리뷰 평점은 1점에서 5점 사이여야 합니다. [입력된 평점: %d]".formatted(rate));
    }
}
