package org.sparta.delivery.order.infrastructure;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.order.domain.ProductInfo;
import org.sparta.delivery.order.domain.service.ProductProvider;
import org.sparta.delivery.store.domain.Product;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.exception.ProductNotFoundException;
import org.sparta.delivery.store.domain.query.ProductQueryRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductProviderImpl implements ProductProvider {

    private final ProductQueryRepository productQueryRepository;

    @Override
    public ProductInfo getProduct(UUID storeId, String productCode) {

        Product product = productQueryRepository.findByProductCode(StoreId.of(storeId), productCode).orElseThrow(ProductNotFoundException::new);

        return ProductInfo.builder()
                .code(product.getProductCode())
                .name(product.getName())
                .price(product.getPrice().getValue())
                .orderable(product.isOrderable())
                .build();
    }
}
