package org.sparta.delivery.store.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.store.domain.*;
import org.sparta.delivery.store.domain.service.OwnerCheck;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class StoreDto {
    @Getter
    @Builder
    public static class OperationDto {
        private RoleCheck roleCheck;
        private OwnerCheck ownerCheck;
        private DayOfWeek dayOfWeek;
        private LocalTime startHour;
        private LocalTime endHour;
        private LocalTime breakStart1;
        private LocalTime breakEnd1;
        private LocalTime breakStart2;
        private LocalTime breakEnd2;
    }

    // OperationDto -> StoreOperation
    public static StoreOperation toOperation(OperationDto dto) {
        return StoreOperation.builder()
                .startHour(dto.getStartHour())
                .endHour(dto.getEndHour())
                .breakHour1(new BreakTime(dto.getBreakStart1(), dto.getBreakEnd1()))
                .breakHour2(new BreakTime(dto.getBreakStart1(), dto.getBreakEnd2()))
                .build();
    }

    @Getter
    @Builder
    public static class ProductDto {
        private RoleCheck roleCheck;
        private OwnerCheck ownerCheck;
        private String productCode;
        private UUID categoryId;
        private String name;
        private int price;
        private List<ProductOptionDto> options;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProductOptionDto {
        private String name;
        private int price;
        private List<ProductSubOptionDto> subOptions;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProductSubOptionDto {
        private String name;
        private int addPrice;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CategoryDto {
        private RoleCheck roleCheck;
        private OwnerCheck ownerCheck;
        private List<UUID> categoryIds;
    }

    // ProductDto -> Product
    public static Product toProduct(ProductDto dto) {
        List<ProductOptionDto> optionDtos = dto.getOptions();
        List<ProductOption> options =optionDtos == null ? null : optionDtos.stream().map(StoreDto::toProductOption).toList();
        return Product.builder()
                .productCode(dto.productCode)
                .name(dto.getName())
                .price(dto.getPrice())
                .categoryId(dto.getCategoryId())
                .options(options)
                .build();
    }

    // ProductOptionDto -> ProductOption
    public  static ProductOption toProductOption(ProductOptionDto dto) {
       List<ProductSubOptionDto> subOptionDtos = dto.getSubOptions();

       List<ProductSubOption> subOptions = subOptionDtos == null ? null : subOptionDtos.stream().map(s -> new ProductSubOption(s.getName(), s.getAddPrice())).toList();

        return ProductOption
                .builder()
                .name(dto.getName())
                .price(dto.price)
                .subOptions(subOptions)
                .build();
    }
}
