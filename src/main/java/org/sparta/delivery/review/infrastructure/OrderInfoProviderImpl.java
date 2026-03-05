package org.sparta.delivery.review.infrastructure;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.order.domain.Order;
import org.sparta.delivery.order.domain.OrderId;
import org.sparta.delivery.order.domain.OrderItem;
import org.sparta.delivery.order.domain.OrderStatus;
import org.sparta.delivery.order.domain.query.OrderQueryRepository;
import org.sparta.delivery.review.domain.ReviewOrderInfo;
import org.sparta.delivery.review.domain.service.OrderInfoProvider;
import org.sparta.delivery.store.domain.ReviewOrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderInfoProviderImpl implements OrderInfoProvider {

    private final OrderQueryRepository orderQueryRepository;

    @Override
    public ReviewOrderInfo getOrderInfo(UUID orderId) {
        return orderQueryRepository.findById(OrderId.of(orderId))
                .filter(this::isReviewable)
                .map(this::convertToReviewOrderInfo)
                .orElse(null); // Review 엔티티에서 null 체크 후 예외 발생
    }

    // 주문 상품이 없다면 정상적인 주문이 아니므로 리뷰 작성 불가
    // 주문 완료 상태(ORDER_DONE)이 아니라면 리뷰 작성 불가
    private boolean isReviewable(Order order) {
        List<OrderItem> items = order.getOrderItems();
        return items != null && !items.isEmpty() && order.getStatus() == OrderStatus.ORDER_DONE;
    }

    private ReviewOrderInfo convertToReviewOrderInfo(Order order) {
        List<ReviewOrderItem> reviewItems = order.getOrderItems().stream()
                .map(i -> new ReviewOrderItem(
                        i.getItem().getName(),
                        i.getTotalPrice().getValue()
                ))
                .toList();

        return ReviewOrderInfo.builder()
                .orderId(order.getId().getId())
                .storeId(order.getStoreInfo().getStoreId())
                .storeName(order.getStoreInfo().getStoreName())
                .items(reviewItems)
                .build();
    }
}
