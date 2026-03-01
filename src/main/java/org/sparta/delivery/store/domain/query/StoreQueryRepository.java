package org.sparta.delivery.store.domain.query;

import org.sparta.delivery.store.domain.Store;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.query.dto.StoreQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StoreQueryRepository {
    Optional<Store> findById(StoreId id); // 매장 한개조회
    Page<Store> findAll(StoreQueryDto.Search search, Pageable pageable); // 매장 검색
    Page<Store> findAllNearest(double latitude, double longitude, double radiusKm, Pageable pageable);  // 현재 좌표에서 몇 km 반경에 가장 가까운 매장 조회

}
