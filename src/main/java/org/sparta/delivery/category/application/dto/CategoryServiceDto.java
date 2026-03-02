package org.sparta.delivery.category.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class CategoryServiceDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Category {
        private UUID id; // 카테고리 ID
        private String name; // 카테고리 명
    }
}
