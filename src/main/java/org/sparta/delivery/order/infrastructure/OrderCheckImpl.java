package org.sparta.delivery.order.infrastructure;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.order.domain.OrderItem;
import org.sparta.delivery.order.domain.service.OrderCheck;
import org.sparta.delivery.store.domain.Product;
import org.sparta.delivery.store.domain.Store;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.StoreRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderCheckImpl implements OrderCheck {

    private final StoreRepository storeRepository;

    @Override
    public boolean isOrderable(UUID storeId, List<OrderItem> items) {
        // 매장 존재 여부 및 기본 상태 확인
        Store store = storeRepository.findById(StoreId.of(storeId))
                .orElse(null);

        // 매장이 없거나, 노출되지 않는 상태(PREPARING/OPEN이 아님)라면 주문 불
        if (store == null || !store.isVisible()) {
            return false;
        }

        // 매장의 실시간 주문 가능 여부 확인 (영업 시간 및 브레이크 타임 체크)
        if (!store.isOrderable()) {
            return false;
        }

        // 주문 항목(Items) 검증
        if (items == null || items.isEmpty()) {
            return false;
        }

        return items.stream().allMatch(orderItem -> {
            // 매장 내부에서 해당 상품(ProductCode 기반)을 조회하여 주문 가능 상태인지 확인
            Product product = store.getProduct(orderItem.getItem().getCode());
            return product != null && product.isOrderable();
        });
    }
}
