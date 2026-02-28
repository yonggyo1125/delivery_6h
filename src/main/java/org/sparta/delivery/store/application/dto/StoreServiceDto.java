package org.sparta.delivery.store.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class StoreServiceDto {
    @Getter
    @Builder
    public static class CreateDto {
        private UUID storeId;
        private UUID ownerId;
        private String ownerName;
        private String landline;
        private String email;
        private String address;
        private List<UUID> categoryId;
    }


}
