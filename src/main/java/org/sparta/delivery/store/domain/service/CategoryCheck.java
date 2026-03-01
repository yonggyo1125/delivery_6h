package org.sparta.delivery.store.domain.service;

import org.sparta.delivery.store.domain.StoreId;

import java.util.List;
import java.util.UUID;

public interface CategoryCheck {
    boolean exists(List<UUID> categoryIds); // 매장에 카테고리 등록,수정 요청시 모든 카테고리가 존재하는지 체크
    boolean existsInStore(StoreId storeId, UUID categoryId); // 상품등록,수정시 매장이 가지고 있는 분류인지 체크
}
