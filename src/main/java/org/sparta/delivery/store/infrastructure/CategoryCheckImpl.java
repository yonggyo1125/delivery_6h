package org.sparta.delivery.store.infrastructure;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.service.CategoryCheck;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryCheckImpl implements CategoryCheck {
    @Override
    public boolean exists(List<UUID> categoryId) {
        return false;
    }

    @Override
    public boolean existsInStore(StoreId storeId, UUID categoryId) {
        return false;
    }
}
