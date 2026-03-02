package org.sparta.delivery.category.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "카테고리 응답 DTO")
public class CategoryResponseDto {

    @Schema(description = "처리된 카테고리 ID 목록")
    private List<UUID> categoryIds;

    @Getter
    @Builder
    @Schema(description = "카테고리 상세 정보")
    public static class Detail {
        @Schema(description = "카테고리 ID")
        private UUID id;
        @Schema(description = "카테고리 명")
        private String name;
    }

    public static CategoryResponseDto of(List<UUID> ids) {
        return CategoryResponseDto.builder()
                .categoryIds(ids)
                .build();
    }
}
