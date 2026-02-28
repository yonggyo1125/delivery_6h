package org.sparta.delivery.store.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.store.domain.Store;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.StoreRepository;
import org.sparta.delivery.store.domain.exception.StoreNotFoundException;
import org.sparta.delivery.store.domain.service.OwnerCheck;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RemoveStoreService {
    private final RoleCheck roleCheck;
    private final OwnerCheck ownerCheck;
    private final StoreRepository repository;

    @Transactional
    public void remove(UUID storeId) {
        Store store = repository.findById(StoreId.of(storeId)).orElseThrow(StoreNotFoundException::new);

        store.remove(roleCheck, ownerCheck);
    }

}
