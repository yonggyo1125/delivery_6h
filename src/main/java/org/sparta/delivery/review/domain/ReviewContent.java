package org.sparta.delivery.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.*;
import org.sparta.delivery.review.domain.exception.ReviewRateOutOfRangeException;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewContent {

    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false)
    private String content;

    private int score; // 평점

    @Builder
    protected ReviewContent(String subject, String content, int score) {
        this.subject = subject;
        this.content = content;

        setScore(score);
    }

    private void setScore(int score) {
        // 리뷰의 평점은 필수이며 1~5점 사이 선택
        if (score < 1 || score > 5) {
            throw new ReviewRateOutOfRangeException(score);
        }

        this.score = score;
    }
}
