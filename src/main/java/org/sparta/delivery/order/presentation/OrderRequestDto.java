package org.sparta.delivery.order.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.sparta.delivery.order.application.dto.OrderServiceDto;
import org.sparta.delivery.order.domain.query.OrderQueryDto;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "주문 도메인 통합 요청 DTO")
public class OrderRequestDto {

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    @Schema(description = "주문 생성을 위한 요청 데이터")
    public static class Create {
        @Schema(description = "주문자 실명 (미입력 시 회원 정보 사용)", example = "김르탄")
        private String ordererName;

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Schema(description = "주문자 이메일 (미입력 시 회원 정보 사용)", example = "yonggyo@sparta.com")
        private String ordererEmail;

        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "연락처는 010-0000-0000 형식이어야 합니다.")
        @Schema(description = "주문자 연락처 (미입력 시 회원 정보 사용)", example = "010-1234-5678")
        private String ordererMobile;

        @NotNull(message = "매장 ID는 필수 입력 값입니다.")
        @Schema(description = "주문 대상 매장의 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID storeId;

        @Schema(description = "매장명 (Snapshot용)", example = "스파르타 치킨")
        private String storeName;

        @Schema(description = "매장 주소 (Snapshot용)")
        private String storeAddress;

        @Schema(description = "매장 전화번호 (Snapshot용)")
        private String storeTel;

        @NotBlank(message = "배송지 주소는 필수 입력 값입니다.")
        @Schema(description = "배송할 기본 주소", example = "서울특별시 강남구 테헤란로 427")
        private String deliveryAddress;

        @Schema(description = "상세 주소 (동, 호수 등)", example = "스파르타 빌딩 6층")
        private String deliveryAddressDetail;

        @Schema(description = "배송 시 요청사항", example = "문 앞에 두고 벨 눌러주세요.")
        private String deliveryMemo;

        @Valid
        @NotEmpty(message = "최소 1개 이상의 상품을 주문해야 합니다.") // NotNull보다 리스트 검증에 적합
        @Schema(description = "주문 항목 리스트")
        private List<Item> items;

        public OrderServiceDto.Create toServiceDto() {
            return OrderServiceDto.Create.builder()
                    .ordererName(ordererName).ordererEmail(ordererEmail).ordererMobile(ordererMobile)
                    .storeId(storeId).storeName(storeName).storeAddress(storeAddress).storeTel(storeTel)
                    .deliveryAddress(deliveryAddress).deliveryAddressDetail(deliveryAddressDetail).deliveryMemo(deliveryMemo)
                    .items(items.stream().map(Item::toServiceDto).toList())
                    .build();
        }
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    @Schema(description = "주문 개별 상품 정보")
    public static class Item {
        @NotBlank(message = "상품 코드는 필수입니다.")
        @Schema(description = "상품 식별 코드", example = "PROD-001")
        private String itemCode;

        @Min(value = 1, message = "주문 수량은 최소 1개 이상이어야 합니다.")
        @Schema(description = "주문 수량", example = "1")
        private int quantity;

        @Valid
        @Schema(description = "사용자가 선택한 옵션 목록")
        private List<Option> options;

        public OrderServiceDto.Item toServiceDto() {
            return OrderServiceDto.Item.builder()
                    .itemCode(itemCode).quantity(quantity)
                    .options(options != null ? options.stream().map(Option::toServiceDto).toList() : List.of())
                    .build();
        }
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    @Schema(description = "선택한 옵션 정보")
    public static class Option {
        @NotBlank(message = "옵션 명칭은 필수입니다.")
        @Schema(description = "옵션 그룹명", example = "맵기 조절")
        private String name;

        @Schema(description = "옵션 추가 가격", example = "500")
        private int price;

        @Valid
        @Schema(description = "선택한 하위 옵션 상세")
        private List<SubOption> subOptions;

        public OrderServiceDto.Option toServiceDto() {
            return OrderServiceDto.Option.builder()
                    .name(name).price(price)
                    .subOptions(subOptions != null ? subOptions.stream().map(SubOption::toServiceDto).toList() : List.of())
                    .build();
        }
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    @Schema(description = "선택한 하위 옵션 정보")
    public static class SubOption {
        @NotBlank(message = "하위 옵션 명칭은 필수입니다.")
        @Schema(description = "하위 옵션 항목명", example = "아주 매운맛")
        private String name;

        @Schema(description = "하위 옵션 추가 가격", example = "0")
        private int price;

        public OrderServiceDto.SubOption toServiceDto() {
            return OrderServiceDto.SubOption.builder().name(name).price(price).build();
        }
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    @Schema(description = "배송지 정보 변경 요청 데이터")
    public static class ChangeDelivery {
        @NotBlank(message = "변경할 주소는 필수 입력 값입니다.")
        @Schema(description = "새로운 배송 주소", example = "서울특별시 강남구 역삼동 123")
        private String address;

        @Schema(description = "새로운 상세 주소", example = "그린빌라 201호")
        private String addressDetail;

        @Schema(description = "변경할 배송 메모", example = "부재 시 경비실에 맡겨주세요.")
        private String memo;

        public OrderServiceDto.ChangeDelivery toServiceDto(UUID orderId) {
            return OrderServiceDto.ChangeDelivery.builder()
                    .orderId(orderId).address(address).addressDetail(addressDetail).memo(memo)
                    .build();
        }
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    @Schema(description = "주문 목록 필터링 검색 조건")
    public static class Search {

        @Schema(description = "주문 ID 목록", example = "[\"550e8400-e29b-41d4-a716-446655440000\"]", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private List<UUID> orderIds;

        @Schema(description = "주문자 성함 (부분 일치)", example = "김르탄", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String ordererName;

        @Schema(description = "주문자 연락처", example = "010-1234-5678", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String ordererMobile;

        @Schema(description = "주문자 이메일", example = "yonggyo@sparta.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String ordererEmail;

        @Schema(description = "매장 UUID 목록", example = "[\"550e8400-e29b-41d4-a716-446655440000\"]", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private List<UUID> storeIds;

        @Schema(description = "매장명 (부분 일치)", example = "치킨", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String storeName;

        @Schema(description = "배송지 주소 (부분 일치)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String deliveryAddress;

        @Schema(description = "주문 상태 필터", example = "[\"ORDER_ACCEPT\", \"DELIVERY\"]", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private List<String> orderStatuses;

        public OrderQueryDto.Search toQuerySearch() {
            return OrderQueryDto.Search.builder()
                    .orderIds(this.orderIds).ordererName(this.ordererName)
                    .ordererMobile(this.ordererMobile).ordererEmail(this.ordererEmail)
                    .storeIds(this.storeIds).storeName(this.storeName)
                    .deliveryAddress(this.deliveryAddress).orderStatuses(this.orderStatuses)
                    .build();
        }
    }
}