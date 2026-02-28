package org.sparta.delivery.store.application.product;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.store.application.dto.StoreServiceDto;
import org.sparta.delivery.store.domain.*;
import org.sparta.delivery.store.domain.dto.StoreDto;
import org.sparta.delivery.store.domain.exception.ProductNotFoundException;
import org.sparta.delivery.store.domain.exception.StoreNotFoundException;
import org.sparta.delivery.store.domain.service.OwnerCheck;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // 유틸리티 클래스 인스턴스화 방지
public class ProductServiceHelper {

    public static StoreDto.ProductDto toProduct(RoleCheck roleCheck, OwnerCheck ownerCheck, StoreServiceDto.Product dto) {
        return StoreDto.ProductDto.builder()
                .roleCheck(roleCheck)
                .ownerCheck(ownerCheck)
                .productCode(dto.getProductCode())
                .categoryId(dto.getCategoryId())
                .name(dto.getName())
                .price(dto.getPrice())
                .options(toOptions(dto.getOptions()))
                .build();
    }

    private static List<StoreDto.ProductOptionDto> toOptions(List<StoreServiceDto.ProductOption> optionsDto) {
        return Optional.ofNullable(optionsDto)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ProductServiceHelper::toOptionDto)
                .toList();
    }

    private static StoreDto.ProductOptionDto toOptionDto(StoreServiceDto.ProductOption optionDto) {
        return StoreDto.ProductOptionDto.builder()
                .name(optionDto.getName())
                .price(optionDto.getPrice())
                .subOptions(toSubOptions(optionDto.getSubOptions()))
                .build();
    }

    private static List<StoreDto.ProductSubOptionDto> toSubOptions(List<StoreServiceDto.ProductSubOption> subOptionsDto) {
        return Optional.ofNullable(subOptionsDto)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(s -> new StoreDto.ProductSubOptionDto(s.getName(), s.getAddPrice()))
                .toList();
    }

    /**
     * StoreServiceDto.ProductOption -> 도메인 ProductOption 변환
     */
    public static ProductOption toProductOptionEntity(StoreServiceDto.ProductOption dto) {
        return ProductOption.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .subOptions(toProductSubOptionEntities(dto.getSubOptions()))
                .build();
    }

    /**
     * StoreServiceDto.ProductSubOption 리스트 -> 도메인 ProductSubOption 리스트 변환
     */
    public static List<ProductSubOption> toProductSubOptionEntities(List<StoreServiceDto.ProductSubOption> subOptionsDto) {
        return Optional.ofNullable(subOptionsDto)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(s -> new ProductSubOption(s.getName(), s.getAddPrice()))
                .toList();
    }

    /**
     * StoreServiceDto.ProductOption 리스트 -> 도메인 ProductOption 리스트 변환 (일괄 변환용)
     */
    public static List<ProductOption> toProductOptionEntities(List<StoreServiceDto.ProductOption> optionsDto) {
        return Optional.ofNullable(optionsDto)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ProductServiceHelper::toProductOptionEntity)
                .toList();
    }

    // 상점 정보 조회
    public static Store getStore(UUID storeId, StoreRepository repository, RoleCheck roleCheck, OwnerCheck ownerCheck) {
        Store store = repository.findById(StoreId.of(storeId)).orElseThrow(StoreNotFoundException::new);
        store.checkAuthority(roleCheck, ownerCheck);

        return store;
    }

    // 상품 정보 조회
    public static Product getProduct(Store store, String productCode) {
        return Optional.of(store.getProduct(productCode)).orElseThrow(ProductNotFoundException::new);
    }
}
