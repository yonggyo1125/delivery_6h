package org.sparta.delivery.review.domain.service;

import java.util.UUID;

// 매장별 리뷰 평점 평균
public interface StoreRatingCalculator {
    double getAverageRating(UUID storeId);
}
