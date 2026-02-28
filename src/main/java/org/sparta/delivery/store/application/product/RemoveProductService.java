package org.sparta.delivery.store.application.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.store.domain.Product;
import org.sparta.delivery.store.domain.Store;
import org.sparta.delivery.store.domain.StoreRepository;
import org.sparta.delivery.store.domain.service.OwnerCheck;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoveProductService {
    private final RoleCheck roleCheck;
    private final OwnerCheck ownerCheck;
    private final StoreRepository repository;

    // 단일 상품 삭제
    @Transactional
    public void remove(UUID storeId, String productCode) {
        Store store = ProductServiceHelper.getStore(storeId, repository, roleCheck, ownerCheck);
        Product product = ProductServiceHelper.getProduct(store, productCode);
        product.remove();

        log.info("Product removed from store {}: {}", storeId, productCode);
    }

    // 여러 상품 삭제
    @Transactional
    public void remove(UUID storeId, List<String> productCodes) {
        Store store = ProductServiceHelper.getStore(storeId, repository, roleCheck, ownerCheck);

        productCodes.forEach(code -> {
            Product product = ProductServiceHelper.getProduct(store, code);
            product.remove();
        });

        log.info("Products removed from store {}: {}", storeId, productCodes);
    }
}
