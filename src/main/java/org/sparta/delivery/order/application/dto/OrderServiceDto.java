package org.sparta.delivery.order.application.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderServiceDto {

    @Getter
    @Builder
    public static class Create {
        private String ordererName;
        private String ordererMobile;
        private String ordererEmail;
        private UUID storeId;
        private String storeName;
        private String storeAddress;
        private String storeTel;
        private String deliveryAddress;
        private String deliveryAddressDetail;
        private String deliveryMemo;
        private List<Item> items;
    }

    @Getter
    @Builder
    public static class Item {
        private String itemCode;
        private int price;
        private int quantity;
        private List<Option> options;
    }

    @Getter
    @Builder
    public static class Option {
        private String name;
        private int price;
        private List<SubOption> subOptions;
    }

    @Getter
    @Builder
    public static class SubOption {
        private String name;
        private int price;
    }

    @Getter
    @Builder
    public static class ChangeDelivery {
        private UUID orderId;
        private String address;
        private String addressDetail;
        private String memo;
    }
}