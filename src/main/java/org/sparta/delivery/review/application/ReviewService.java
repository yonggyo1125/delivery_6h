package org.sparta.delivery.review.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.global.domain.service.UserDetails;
import org.sparta.delivery.global.infrastructure.event.Events;
import org.sparta.delivery.review.domain.Review;
import org.sparta.delivery.review.domain.ReviewId;
import org.sparta.delivery.review.domain.ReviewRepository;
import org.sparta.delivery.review.domain.event.ReviewScoreChangedEvent;
import org.sparta.delivery.review.domain.exception.ReviewNotFoundException;
import org.sparta.delivery.review.domain.service.OrderInfoProvider;
import org.sparta.delivery.review.domain.service.ReviewerCheck;
import org.sparta.delivery.review.domain.service.StoreRatingCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewerCheck reviewerCheck;
    private final RoleCheck roleCheck;
    private final UserDetails userDetails;
    private final OrderInfoProvider orderInfoProvider;
    private final StoreRatingCalculator calculator;
    private final ReviewRepository reviewRepository;

    // 리뷰 작성
    @Transactional
    public UUID create(ReviewServiceDto.Create dto) {

        Review review = Review.builder()
                .orderId(dto.getOrderId())
                .subject(dto.getSubject())
                .content(dto.getContent())
                .score(dto.getScore())
                .orderInfoProvider(orderInfoProvider)
                .rolecheck(roleCheck)
                .reviewerCheck(reviewerCheck)
                .userDetails(userDetails)
                .build();

        reviewRepository.save(review);

        // 이벤트 발행
        triggerEvent(review.getInfo().getStoreId());

        return review.getId().getId();
    }

    // 리뷰 수정
    @Transactional
    public void change(ReviewServiceDto.Change dto) {
        Review review = getReview(dto.getReviewId());
        review.change(dto.getSubject(), dto.getContent(), dto.getScore(), reviewerCheck, roleCheck);

        // 이벤트 발행
        triggerEvent(review.getInfo().getStoreId());
    }

    // 리뷰 삭제
    @Transactional
    public void remove(UUID reviewId) {
        Review review = getReview(reviewId);
        review.remove(reviewerCheck, roleCheck);

        // 이벤트 발행
        triggerEvent(review.getInfo().getStoreId());
    }


    private Review getReview(UUID reviewId) {
        return reviewRepository.findById(ReviewId.of(reviewId)).orElseThrow(ReviewNotFoundException::new);
    }

    // 매장 리뷰 평균 평점 업데이트(이벤트 발행)
    private void triggerEvent(UUID storeId) {
        // 평점 평균 구하기전 리뷰 먼저 반영
        reviewRepository.flush();


        Events.trigger(new ReviewScoreChangedEvent(storeId, calculator.getAverageRating(storeId)));
    }
}
