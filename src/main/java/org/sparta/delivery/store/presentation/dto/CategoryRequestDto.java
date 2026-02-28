package org.sparta.delivery.store.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Schema(description = "매장 카테고리 조작 요청 DTO")
public class CategoryRequestDto {

    @Getter
    @NoArgsConstructor
    @Schema(description = "카테고리 ID 리스트 요청")
    public static class Update {
        @Schema(description = "조작할 카테고리 UUID 리스트", example = "[\"550e8400-e29b-41d4-a716-446655440000\"]")
        @NotEmpty(message = "카테고리 ID는 최소 하나 이상이어야 합니다.")
        private List<UUID> categoryIds;
    }
}
