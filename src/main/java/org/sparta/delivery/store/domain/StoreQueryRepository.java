package org.sparta.delivery.store.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreQueryRepository {
    Page<Store> findNearestStores(double latitude, double longitude, double radiusKm, Pageable pageable);  // 현재 좌표에서 몇 km 반경에 가장 가까운 매장 조회

}
