package org.sparta.delivery.review.domain.query;

import org.sparta.delivery.review.domain.Review;
import org.sparta.delivery.review.domain.ReviewId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ReviewQueryRepository {
    // 리뷰 조회
    Optional<Review> findById(ReviewId orderId);


    // 매장별 리뷰목록 조회
    Page<Review> findAllByStore(UUID storeId, ReviewQueryDto.Search search, Pageable pageable);

    // 사용자별 리뷰목록 조회
    Page<Review> findAllByUser(UUID userId, ReviewQueryDto.Search search, Pageable pageable);

    // 리뷰 목록 조회
    Page<Review> findAll(ReviewQueryDto.Search search, Pageable pageable);
}
