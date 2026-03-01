package org.sparta.delivery.store.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, StoreId>, QuerydslPredicateExecutor<Store> {
    boolean existsByStoreIdAndCategoryId(StoreId storeId, UUID categoryId);
}
