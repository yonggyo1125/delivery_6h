package org.sparta.delivery.store.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class StoreServiceDto {
    @Getter
    @Builder
    public static class CreateStore {
        private UUID storeId;
        private UUID ownerId;
        private String ownerName;
        private String landline;
        private String email;
        private String address;
        private List<UUID> categoryId;
    }

    // 매장 정보
    @Getter
    @Builder
    public static class StoreInfo {
        private String ownerName;
        private String businessNo;
        private String landline;
        private String email;
        private String address;
    }

    // 운영 요일 및 시간
    @Getter
    @Builder
    public static class Operation {
        private DayOfWeek dayOfWeek;
        private LocalTime startHour;
        private LocalTime endHour;
        private LocalTime breakStart1;
        private LocalTime breakEnd1;
        private LocalTime breakStart2;
        private LocalTime breakEnd2;
    }

    //// 상품 S
    // 상품
    @Getter
    @Builder
    public static class Product {
        private String productCode;
        private UUID categoryId;
        private String name;
        private int price;
        private List<ProductOption> options;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProductOption {
        private String name;
        private int price;
        private List<ProductSubOption> subOptions;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProductSubOption {
        private String name;
        private int addPrice;
    }
    //// 상품 E
}
