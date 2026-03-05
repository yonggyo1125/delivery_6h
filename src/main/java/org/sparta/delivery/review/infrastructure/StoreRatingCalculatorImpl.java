package org.sparta.delivery.review.infrastructure;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.review.domain.service.StoreRatingCalculator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoreRatingCalculatorImpl implements StoreRatingCalculator {
    @Override
    public double getAverageRating(UUID storeId) {
        return 0;
    }
}
