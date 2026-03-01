package org.sparta.delivery.store.application.query;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.query.StoreQueryRepository;
import org.sparta.delivery.store.domain.query.dto.StoreQueryDto;
import org.sparta.delivery.store.presentation.dto.StoreResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreQueryService {
    private final StoreQueryRepository storeQueryRepository;

    // 단일 조회
    public StoreResponseDto getStore(UUID storeId) {
        return storeQueryRepository.findById(StoreId.of(storeId))
                .map(StoreResponseDto::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다."));
    }

    // 목록 조회 - 조건 검색
    public Page<StoreResponseDto> searchStores(StoreQueryDto.Search search, Pageable pageable) {
        return storeQueryRepository.findAll(search, pageable)
                .map(StoreResponseDto::from);
    }

    // 근처 매장 조회
    public Page<StoreResponseDto> getNearestStores(double lat, double lon, double radius, Pageable pageable) {
        return storeQueryRepository.findAllNearest(lat, lon, radius, pageable)
                .map(StoreResponseDto::from);
    }
}
