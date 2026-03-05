package org.sparta.delivery.review.domain.exception;

import org.sparta.delivery.global.domain.exception.UnAuthorizedException;

public class InvalidReviewerException extends UnAuthorizedException {
    public InvalidReviewerException() {
        super("유효하지 않은 리뷰 작성자입니다. 다시 로그인해 주세요.");
    }
}
