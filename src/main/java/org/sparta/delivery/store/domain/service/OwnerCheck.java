package org.sparta.delivery.store.domain.service;

import java.util.UUID;

public interface OwnerCheck {
    boolean isOwner(UUID storeId);
}
