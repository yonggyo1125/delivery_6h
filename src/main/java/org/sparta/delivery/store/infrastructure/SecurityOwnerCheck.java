package org.sparta.delivery.store.infrastructure;

import org.sparta.delivery.store.domain.service.OwnerCheck;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityOwnerCheck implements OwnerCheck {
    @Override
    public boolean isOwner(UUID storeId) {
        return false;
    }
}
