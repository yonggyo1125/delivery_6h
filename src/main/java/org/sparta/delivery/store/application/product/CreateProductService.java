package org.sparta.delivery.store.application.product;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.store.application.dto.StoreServiceDto;
import org.sparta.delivery.store.domain.Store;
import org.sparta.delivery.store.domain.StoreRepository;
import org.sparta.delivery.store.domain.service.OwnerCheck;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateProductService {
    private final RoleCheck roleCheck;
    private final OwnerCheck ownerCheck;
    private final StoreRepository repository;

    @Transactional
    public void create(UUID storeId, StoreServiceDto.Product dto) {
        Store store = ProductServiceHelper.getStore(storeId, repository, roleCheck, ownerCheck);

        store.createProduct(ProductServiceHelper.toProduct(roleCheck, ownerCheck, dto));

        repository.save(store);
    }
}
