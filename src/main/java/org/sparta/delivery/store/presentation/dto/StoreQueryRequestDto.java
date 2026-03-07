package org.sparta.delivery.store.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sparta.delivery.store.domain.query.dto.StoreQueryDto;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class StoreQueryRequestDto {

    @Data
    @Schema(description = "매장 통합 검색 요청 객체")
    public static class Search {

        @Schema(
                description = "카테고리 ID 리스트",
                example = "[\"550e8400-e29b-41d4-a716-446655440000\"]",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        private List<UUID> categoryId;

        @Schema(description = "매장명", example = "스파르타 치킨", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String storeName;

        @Schema(description = "매장 상태 (PREPARING, OPEN, CLOSED, DEFUNCT)", example = "OPEN", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String storeStatus;

        @Schema(description = "연락처 (이메일 혹은 전화번호)", example = "02-1234-5678", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String storeContact;

        @Schema(description = "시/도", example = "서울특별시", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String sido;

        @Schema(
                description = "시/구/군 리스트 (sido와 함께 사용)",
                example = "[\"강남구\", \"서초구\"]",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        private List<String> sigugun;

        @Schema(description = "통합 키워드 (이름, 이메일, 전화번호 포함 검색)", example = "치킨", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String keyword;

        public StoreQueryDto.Search toSearchCondition() {
            return StoreQueryDto.Search.builder()
                    .categoryId(this.categoryId)
                    .storeName(this.storeName)
                    .storeStatus(this.storeStatus)
                    .storeContact(this.storeContact)
                    .sido(this.sido)
                    .sigugun(this.sigugun)
                    .keyword(this.keyword)
                    .build();
        }
    }

    @Data
    @Schema(description = "내 주변 매장 조회 요청 객체")
    public static class Nearest {
        @Schema(description = "현재 위도 (Latitude)", example = "37.4979", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double latitude;

        @Schema(description = "현재 경도 (Longitude)", example = "127.0276", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double longitude;

        @Schema(description = "검색 반경 (km)", example = "3.0", defaultValue = "3.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private Double radiusKm = 3.0;
    }
}