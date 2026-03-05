package org.sparta.delivery.review.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.review.domain.QReview;
import org.sparta.delivery.review.domain.service.StoreRatingCalculator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoreRatingCalculatorImpl implements StoreRatingCalculator {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public double getAverageRating(UUID storeId) {
        QReview review = QReview.review;

        Double avg = jpaQueryFactory.select(review.content.score.avg())
                .from(review)
                .where(review.info.storeId.eq(storeId))
                .fetchOne();

        return avg == null ? 0.0 : avg;
    }
}
