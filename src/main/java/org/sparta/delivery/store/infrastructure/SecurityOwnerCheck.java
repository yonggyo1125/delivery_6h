package org.sparta.delivery.store.infrastructure;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.store.domain.QStore;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.StoreRepository;
import org.sparta.delivery.store.domain.service.OwnerCheck;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityOwnerCheck implements OwnerCheck {
    private final StoreRepository repository;
    @Override
    public boolean isOwner(StoreId storeId) {
        if (storeId == null) return false;

        UUID ownerId = getOwnerId();
        if (ownerId == null) return false; // 로그인하지 않은 경우

        QStore store = QStore.store;

        return repository.exists(store.id.eq(storeId).and(store.owner.id.eq(ownerId)));
    }

    @Override
    public UUID getOwnerId() {
        Jwt jwt = getJwt();
        if (jwt != null) {
            try {
                return UUID.fromString(jwt.getSubject());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        return null;
    }

    @Override
    public String getOwnerName() {
        Jwt jwt = getJwt();
        if (jwt != null) {
            String firstName = jwt.getClaimAsString("first_name");
            String lastName = jwt.getClaimAsString("last_name");

            return "%s%s".formatted(lastName,firstName);
        }
        return null;
    }

    private Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }
}
