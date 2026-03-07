package org.sparta.delivery.store.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "매장 관련 요청 DTO")
public class StoreRequestDto {

    @Data
    @Schema(description = "매장 신규 등록 요청")
    public static class Create {
        @Schema(description = "매장명", example = "스파르타 치킨", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "매장명은 필수입니다.")
        private String name;

        @Schema(description = "사업자 번호", example = "123-45-67890", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "사업자 번호는 필수입니다.")
        private String businessNo;

        @Schema(description = "매장 전화번호", example = "02-123-4567", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String landline;

        @Schema(description = "매장 이메일", example = "store@sparta.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @Email @NotBlank(message = "이메일 주소는 필수입니다.")
        private String email;

        @Schema(description = "매장 주소", example = "서울시 강남구 테헤란로 123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "매장 주소는 필수입니다.")
        private String address;

        @Schema(
                description = "카테고리 ID 리스트",
                example = "[\"550e8400-e29b-41d4-a716-446655440000\"]",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        private List<UUID> categoryIds;
    }

    @Data
    @Schema(description = "매장 정보 수정 요청")
    public static class ChangeInfo {
        @Schema(description = "점주 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "점주 이름은 필수입니다.")
        private String ownerName;

        @Schema(description = "매장명", example = "스파르타 치킨", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "매장명은 필수입니다.")
        private String name;

        @Schema(description = "매장 설명", example = "스파르타 치킨 설명...", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String description;

        @Schema(description = "사업자 번호", example = "123-45-67890", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "사업자 번호는 필수입니다.")
        private String businessNo;

        @Schema(description = "매장 전화번호", example = "02-123-4567", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String landline;

        @Schema(description = "매장 이메일", example = "update@sparta.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @Email @NotBlank(message = "이메일 주소는 필수입니다.")
        private String email;

        @Schema(description = "매장 주소", example = "서울시 서초구 반포대로 456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "주소는 필수입니다.")
        private String address;
    }

    @Data
    @Schema(description = "매장 운영 시간 요청")
    public static class Operation {
        @Schema(description = "요일", example = "MONDAY", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        private DayOfWeek dayOfWeek;

        @Schema(description = "영업 시작 시간", example = "10:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private LocalTime startHour;

        @Schema(description = "영업 종료 시간", example = "22:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private LocalTime endHour;

        @Schema(description = "브레이크 타임 1 시작", example = "15:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private LocalTime breakStart1;

        @Schema(description = "브레이크 타임 1 종료", example = "16:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private LocalTime breakEnd1;

        @Schema(description = "브레이크 타임 2 시작", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private LocalTime breakStart2;

        @Schema(description = "브레이크 타임 2 종료", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private LocalTime breakEnd2;
    }
}