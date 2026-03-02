package org.sparta.delivery.global.domain.service;

import java.util.UUID;

public interface OwnerCheck {
    boolean isOwner(UUID storeId);
    UUID getOwnerId(); // 매장 주인 로그인 ID
    String getOwnerName(); // 매장 주인명
    UUID getStoreId();
}
