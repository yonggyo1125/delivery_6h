package org.sparta.delivery.store.presentation.dto;

import lombok.Builder;
import org.sparta.delivery.store.domain.Product;
import org.sparta.delivery.store.domain.ProductStatus;
import java.util.List;

@Builder
public record ProductResponseDto(
        String productCode,
        String name,
        int price,
        ProductStatus status,
        List<OptionResponseDto> options
) {
    public static ProductResponseDto from(Product product) {
        return ProductResponseDto.builder()
                .productCode(product.getProductCode())
                .name(product.getName())
                .price(product.getPrice().getValue())
                .status(product.getStatus())
                .options(product.getOptions().stream()
                        .map(OptionResponseDto::from)
                        .toList())
                .build();
    }

    @Builder
    public record OptionResponseDto(String name, int price, List<SubOptionDto> subOptions) {
        public static OptionResponseDto from(org.sparta.delivery.store.domain.ProductOption opt) {
            return OptionResponseDto.builder()
                    .name(opt.getName())
                    .price(opt.getPrice().getValue())
                    .subOptions(opt.getSubOptions().stream()
                            .map(s -> new SubOptionDto(s.name(), s.addPrice()))
                            .toList())
                    .build();
        }
    }

    public record SubOptionDto(String name, int addPrice) {}
}