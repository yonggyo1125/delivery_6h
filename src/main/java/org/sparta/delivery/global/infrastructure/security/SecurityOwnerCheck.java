package org.sparta.delivery.global.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.OwnerCheck;
import org.sparta.delivery.global.domain.service.UserDetails;
import org.sparta.delivery.store.domain.QStore;
import org.sparta.delivery.store.domain.StoreRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityOwnerCheck implements OwnerCheck {
    private final StoreRepository repository;
    private final UserDetails userDetails;

    @Override
    public boolean isOwner(UUID storeId) {
        if (storeId == null) return false;

        UUID ownerId = userDetails.getId();
        if (ownerId == null || !userDetails.isAuthenticated()) {
            return false;
        }

        QStore store = QStore.store;
        return repository.exists(
                store.id.id.eq(storeId)
                        .and(store.owner.id.eq(ownerId))
        );
    }

    @Override
    public UUID getOwnerId() {
        return userDetails.getId();
    }

    @Override
    public String getOwnerName() {
        return userDetails.getName();
    }
}
