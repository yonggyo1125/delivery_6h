package org.sparta.delivery.category.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sparta.delivery.category.application.dto.CategoryServiceDto;

import java.util.UUID;

@Schema(description = "카테고리 요청 DTO")
public class CategoryRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "카테고리 생성/수정 항목")
    public static class CategoryItem {
        @Schema(description = "카테고리 ID (수정 시 필수)", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID id;

        @NotBlank(message = "카테고리명은 필수입니다.")
        @Schema(description = "카테고리 이름", example = "치킨")
        private String name;

        public CategoryServiceDto.Category toServiceDto() {
            return CategoryServiceDto.Category.builder()
                    .id(this.id)
                    .name(this.name)
                    .build();
        }
    }
}