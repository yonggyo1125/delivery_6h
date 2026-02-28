package org.sparta.delivery.store.application.product;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.store.application.dto.StoreServiceDto;
import org.sparta.delivery.store.domain.Product;
import org.sparta.delivery.store.domain.ProductStatus;
import org.sparta.delivery.store.domain.Store;
import org.sparta.delivery.store.domain.StoreRepository;
import org.sparta.delivery.store.domain.service.OwnerCheck;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangeProductService {
    private final RoleCheck roleCheck;
    private final OwnerCheck ownerCheck;
    private final StoreRepository repository;

    // 상품 정보 변경
    @Transactional
    public void changeProductInfo(UUID storeId, StoreServiceDto.Product dto) {
        Store store = ProductServiceHelper.getStore(storeId, repository, roleCheck, ownerCheck);

        store.changeProduct(dto.getProductCode(), ProductServiceHelper.toProduct(roleCheck, ownerCheck, dto));
    }

    // 옵션 추가
    @Transactional
    public void createProductOption(UUID storeId, String productCode, List<StoreServiceDto.ProductOption> options) {
        Store store = ProductServiceHelper.getStore(storeId, repository, roleCheck, ownerCheck);

        Product product = ProductServiceHelper.getProduct(store, productCode);
        product.createOptions(ProductServiceHelper.toProductOptionEntities(options));
    }

    // 옵션 삭제
    @Transactional
    public void removeProductOption(UUID storeId, String productCode, List<Integer> optionsIdx) {
        Store store = ProductServiceHelper.getStore(storeId, repository, roleCheck, ownerCheck);

        Product product = ProductServiceHelper.getProduct(store, productCode);
        product.removeOptions(optionsIdx);
    }

    // 옵션 비우기
    @Transactional
    public void truncateProductOption(UUID storeId, String productCode) {
        Store store = ProductServiceHelper.getStore(storeId, repository, roleCheck, ownerCheck);

        Product product = ProductServiceHelper.getProduct(store, productCode);
        product.truncateOption();
    }

    // 옵션 교체하기
    @Transactional
    public void replaceProductOption(UUID storeId, String productCode, List<StoreServiceDto.ProductOption> options) {
        Store store = ProductServiceHelper.getStore(storeId, repository, roleCheck, ownerCheck);

        Product product = ProductServiceHelper.getProduct(store, productCode);
        product.replaceOptions(ProductServiceHelper.toProductOptionEntities(options));
    }

    // 상품 상태
    @Transactional
    public void changeProductStatus(UUID storeId, String productCode, String status) {
        Store store = ProductServiceHelper.getStore(storeId, repository, roleCheck, ownerCheck);
        Product product = ProductServiceHelper.getProduct(store, productCode);

        ProductStatus targetStatus = ProductStatus.valueOf(status.toUpperCase());

        switch (targetStatus) {
            case READY -> product.changeReadyStatus();
            case SALE -> product.changeSaleStatus();
            case STOCK_OUT -> product.changeStockOutStatus();
        }
    }
}
