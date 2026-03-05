package org.sparta.delivery.review.application.query;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.review.application.ReviewServiceDto.ReviewDto;
import org.sparta.delivery.review.domain.ReviewId;
import org.sparta.delivery.review.domain.exception.ReviewNotFoundException;
import org.sparta.delivery.review.domain.query.ReviewQueryDto;
import org.sparta.delivery.review.domain.query.ReviewQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {

    private final ReviewQueryRepository reviewQueryRepository;

    // 리뷰 상세 조회
    public ReviewDto getReview(UUID reviewId) {
        return reviewQueryRepository.findById(ReviewId.of(reviewId))
                .map(ReviewDto::from)
                .orElseThrow(ReviewNotFoundException::new);
    }

    // 목록 조회
    public Page<ReviewDto> getReviewsByStore(UUID storeId, ReviewQueryDto.Search search, Pageable pageable) {
        return reviewQueryRepository.findAllByStore(storeId, search, pageable)
                .map(ReviewDto::from);
    }

    public Page<ReviewDto> getReviewsByUser(UUID userId, ReviewQueryDto.Search search, Pageable pageable) {
        return reviewQueryRepository.findAllByUser(userId, search, pageable)
                .map(ReviewDto::from);
    }

    public Page<ReviewDto> getAllReviews(ReviewQueryDto.Search search, Pageable pageable) {
        return reviewQueryRepository.findAll(search, pageable)
                .map(ReviewDto::from);
    }
}