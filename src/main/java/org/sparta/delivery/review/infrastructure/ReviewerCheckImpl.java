package org.sparta.delivery.review.infrastructure;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.UserDetails;
import org.sparta.delivery.order.domain.OrderId;
import org.sparta.delivery.order.domain.exception.OrderNotFoundException;
import org.sparta.delivery.order.domain.query.OrderQueryRepository;
import org.sparta.delivery.review.domain.ReviewId;
import org.sparta.delivery.review.domain.ReviewRepository;
import org.sparta.delivery.review.domain.exception.ReviewNotFoundException;
import org.sparta.delivery.review.domain.service.ReviewerCheck;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewerCheckImpl implements ReviewerCheck {
    private final UserDetails userDetails;
    private final ReviewRepository reviewRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * 1. 리뷰 작성시 주문자와 로그인한 사용자가 같은지 체크
     * 2. 리뷰 수정인 경우 리뷰 작성자가 로그인한 사용자와 같은지도 체크
     *    (주문번호는 최초 등록시에만 수정이 되므로 수정일땐 체크 불필요)
     */
    @Override
    public boolean check(ReviewId reviewId, UUID orderId) {
        UUID currentUserId = userDetails.getId();
        return reviewId == null ? isOrderer(orderId, currentUserId) : isReviewer(reviewId, currentUserId);

    }

    private boolean isOrderer(UUID orderId, UUID userId) {
        return orderQueryRepository.findById(OrderId.of(orderId))
                .map(order -> order.getOrderer().getId().equals(userId))
                .orElseThrow(OrderNotFoundException::new);
    }

    private boolean isReviewer(ReviewId reviewId, UUID userId) {
        return reviewRepository.findById(reviewId)
                .map(review -> review.getReviewer().getId().equals(userId))
                .orElseThrow(ReviewNotFoundException::new);
    }
}
