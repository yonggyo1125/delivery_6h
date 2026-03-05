package org.sparta.delivery.review.application;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sparta.delivery.review.domain.Review;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewServiceDto {

    @Getter
    @Builder
    public static class Create {
        private UUID orderId;
        private String subject;
        private String content;
        private int score;
    }

    @Getter
    @Builder
    public static class Change {
        private UUID reviewId;
        private String subject;
        private String content;
        private int score;
    }

    @Getter
    @Builder
    public static class ReviewDto {
        private UUID reviewId;
        private UUID orderId;
        private UUID storeId;
        private String storeName;
        private String subject;
        private String content;
        private int score;
        private String reviewerName;
        private LocalDateTime createdAt;

        public static ReviewDto from(Review review) {
            return ReviewDto.builder()
                    .reviewId(review.getId().getId())
                    .orderId(review.getInfo().getOrderId())
                    .storeId(review.getInfo().getStoreId())
                    .storeName(review.getInfo().getStoreName())
                    .subject(review.getContent().getSubject())
                    .content(review.getContent().getContent())
                    .score(review.getContent().getScore())
                    .reviewerName(review.getReviewer().getReviewerName())
                    .createdAt(review.getCreatedAt())
                    .build();
        }
    }
}
