package org.sparta.delivery.store.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Schema(description = "상품 관련 요청 DTO")
public class ProductRequestDto {

    @Data
    @Schema(description = "상품 등록 및 수정 요청")
    public static class Save {
        @Schema(description = "상품 관리 코드 (지정되지 않으면 자동 생성)", example = "CHICKEN-001", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String productCode;

        @Schema(description = "카테고리 ID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "카테고리는 필수입니다.")
        private UUID categoryId;

        @Schema(description = "AI 자동 상품명 생성 여부 (true일 경우 AI가 상품명 생성)", example = "false", defaultValue = "false")
        private boolean aiGenerated = false;

        @Schema(description = "AI 상품명 생성을 위한 상세 특징 정보 (AI 생성 시 권장)", example = "매콤한 맛이 강하고 바삭한 식감을 강조한 후라이드 치킨", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String aiContext;

        @Schema(description = "상품명 (aiGenerated가 false일 경우 필수 입력)", example = "황금올리브 치킨",  requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String name;

        @Schema(description = "상품 가격", example = "20000", requiredMode = Schema.RequiredMode.REQUIRED)
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        private int price;

        @Schema(
                description = "상품 옵션 리스트",
                example = "[]",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Valid
        private List<Option> options;
    }

    @Data
    @Schema(description = "상품 옵션 정보")
    public static class Option {
        @Schema(description = "옵션명", example = "맵기 선택", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "옵션명은 필수입니다.")
        private String name;

        @Schema(description = "옵션 기본 추가 금액", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private int price;

        @Schema(
                description = "하위 옵션(세부 선택) 리스트",
                example = "[]",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Valid
        private List<SubOption> subOptions;
    }

    @Data
    @Schema(description = "상품 하위 옵션 정보")
    public static class SubOption {
        @Schema(description = "하위 옵션명", example = "아주 매운맛", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "하위 옵션명은 필수입니다.")
        private String name;

        @Schema(description = "추가 금액", example = "500", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private int addPrice;
    }
}