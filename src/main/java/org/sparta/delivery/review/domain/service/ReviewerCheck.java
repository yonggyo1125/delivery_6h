package org.sparta.delivery.review.domain.service;

import org.sparta.delivery.review.domain.ReviewId;

import java.util.UUID;

/**
 * 1. 리뷰 작성시 주문자와 로그인한 사용자가 같은지 체크
 * 2. 리뷰 수정인 경우 리뷰 작성자가 로그인한 사용자와 같은지도 체크
 *    (주문번호는 최초 등록시에만 수정이 되므로 수정일땐 체크 불필요)
 */
public interface ReviewerCheck {
    boolean check(ReviewId reviewId, UUID orderId);
}
