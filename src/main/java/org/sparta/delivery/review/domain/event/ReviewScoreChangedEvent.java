package org.sparta.delivery.review.domain.event;

import java.util.UUID;

public record ReviewScoreChangedEvent(
        UUID storeId,
        double averageScore
) {}
