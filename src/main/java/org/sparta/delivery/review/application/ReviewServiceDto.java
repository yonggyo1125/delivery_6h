package org.sparta.delivery.review.application;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
