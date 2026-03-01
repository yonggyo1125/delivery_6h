package org.sparta.delivery.store.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sparta.delivery.store.domain.Store;
import org.sparta.delivery.store.domain.StoreStatus;

import java.util.UUID;

@Schema(description = "매장 상세 정보 응답 객체 (불변 record)")
public record StoreResponseDto(
        @Schema(description = "매장 식별자", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "매장명", example = "스파르타 치킨 강남점")
        String name,

        @Schema(description = "매장 상태", example = "OPEN")
        StoreStatus status,

        @Schema(description = "매장 유선 번호", example = "02-123-4567")
        String landline,

        @Schema(description = "매장 대표 이메일", example = "owner@spartachicken.com")
        String email,

        @Schema(description = "매장 도로명 주소", example = "서울특별시 강남구 테헤란로 123")
        String address,

        @Schema(description = "사업자 등록 번호", example = "123-45-67890")
        String businessNo,

        @Schema(description = "가맹점주 성함", example = "김르탄")
        String ownerName
) {
    /**
     * 정적 팩토리 메서드를 통해 엔티티 결합도를 서비스 계층으로 이동
     */
    public static StoreResponseDto from(Store store) {
        return new StoreResponseDto(
                store.getId().getId(),
                store.getName(),
                store.getStatus(),
                store.getContact().getLandline(),
                store.getContact().getEmail(),
                store.getLocation().getAddress(),
                store.getBusinessNo(),
                store.getOwner().getName()
        );
    }
}