package org.sparta.delivery.store.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Schema(description = "상품 관련 요청 DTO")
public class ProductRequestDto {

    @Getter
    @NoArgsConstructor
    @Schema(description = "상품 등록 및 수정 요청")
    public static class Save {
        @Schema(description = "상품 관리 코드", example = "CHICKEN-001")
        @NotBlank(message = "상품 코드는 필수입니다.")
        private String productCode;

        @Schema(description = "카테고리 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "카테고리는 필수입니다.")
        private UUID categoryId;

        @Schema(description = "상품명", example = "황금올리브 치킨")
        @NotBlank(message = "상품명은 필수입니다.")
        private String name;

        @Schema(description = "상품 가격", example = "20000")
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        private int price;

        @Schema(description = "상품 옵션 리스트")
        @Valid
        private List<Option> options;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "상품 옵션 정보")
    public static class Option {
        @Schema(description = "옵션명", example = "맵기 선택")
        @NotBlank(message = "옵션명은 필수입니다.")
        private String name;

        @Schema(description = "옵션 기본 추가 금액", example = "0")
        private int price;

        @Schema(description = "하위 옵션(세부 선택) 리스트")
        @Valid
        private List<SubOption> subOptions;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "상품 하위 옵션 정보")
    public static class SubOption {
        @Schema(description = "하위 옵션명", example = "아주 매운맛")
        @NotBlank(message = "하위 옵션명은 필수입니다.")
        private String name;

        @Schema(description = "추가 금액", example = "500")
        private int addPrice;
    }
}