package org.sparta.delivery.store.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "상품 조회 요청 파라미터")
public record ProductQueryRequestDto(
        @Schema(description = "상품명 검색", example = "후라이드 치킨")
        String name,

        @Schema(description = "상품 코드 목록 (IN 조건)")
        List<String> productCodes,

        @Schema(description = "카테고리 ID 목록 (IN 조건)")
        List<UUID> categoryIds,

        @Schema(description = "통합 검색 키워드 (상품명 또는 코드)", example = "CHICKEN")
        String keyword
) {}