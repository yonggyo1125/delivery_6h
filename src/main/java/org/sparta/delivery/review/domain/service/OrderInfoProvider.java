package org.sparta.delivery.review.domain.service;

import org.sparta.delivery.review.domain.ReviewOrderInfo;

import java.util.UUID;

public interface OrderInfoProvider {
    ReviewOrderInfo getOrderInfo(UUID orderId);
}
