package org.sparta.delivery.store.domain.service;

import org.sparta.delivery.store.domain.StoreId;

import java.util.UUID;

public interface OwnerCheck {
    boolean isOwner(StoreId storeId);
    UUID getOwnerId(); // 매장 주인 로그인 ID
    String getOwnerName(); // 매장 주인명
}
