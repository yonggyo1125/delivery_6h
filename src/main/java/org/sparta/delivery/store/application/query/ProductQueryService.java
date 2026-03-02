package org.sparta.delivery.store.application.query;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.exception.ProductNotFoundException;
import org.sparta.delivery.store.domain.query.ProductQueryRepository;
import org.sparta.delivery.store.domain.query.dto.ProductQueryDto;
import org.sparta.delivery.store.presentation.dto.ProductResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductQueryRepository productQueryRepository;

    public ProductResponseDto getProduct(UUID storeId, String productCode) {
        return productQueryRepository.findByProductCode(StoreId.of(storeId), productCode)
                .map(ProductResponseDto::from)
                .orElseThrow(ProductNotFoundException::new);
    }

    public List<ProductResponseDto> getProducts(UUID storeId, ProductQueryDto.Search search) {
        return productQueryRepository.findAll(StoreId.of(storeId), search)
                .stream()
                .map(ProductResponseDto::from)
                .toList();
    }
}