package org.sparta.delivery.order.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.sparta.delivery.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "주문 도메인 통합 응답 DTO")
public class OrderResponseDto {

    @Getter @Builder @AllArgsConstructor
    @Schema(description = "주문 생성 처리 결과")
    public static class CreateResult {
        @Schema(description = "생성된 주문의 고유 식별자 (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID orderId;
    }

    @Getter @Builder @AllArgsConstructor
    @Schema(description = "주문 목록 조회를 위한 요약 데이터")
    public static class Order {
        @Schema(description = "주문 UUID")
        private UUID orderId;
        @Schema(description = "매장 명칭")
        private String storeName;
        @Schema(description = "최종 주문 총액")
        private int totalOrderPrice;
        @Schema(description = "현재 주문 상태")
        private OrderStatus status;
        @Schema(description = "주문 일시")
        private LocalDateTime createdAt;
    }

    @Getter @Builder @AllArgsConstructor
    @Schema(description = "주문 상세 조회를 위한 전체 데이터")
    public static class OrderDetail {
        @Schema(description = "주문 UUID")
        private UUID orderId;
        @Schema(description = "매장 명칭")
        private String storeName;
        @Schema(description = "주문자 성함")
        private String ordererName;
        @Schema(description = "배송 기본 주소")
        private String deliveryAddress;
        @Schema(description = "배송 요청 사항")
        private String deliveryMemo;
        @Schema(description = "최종 결제 금액")
        private int totalOrderPrice;
        @Schema(description = "현재 주문 상태")
        private OrderStatus status;
        @Schema(description = "주문 상품 상세 목록")
        private List<OrderItem> items;
        @Schema(description = "주문 생성 일시")
        private LocalDateTime createdAt;
    }

    @Getter @Builder @AllArgsConstructor
    @Schema(description = "주문된 개별 상품 상세 내역")
    public static class OrderItem {
        @Schema(description = "상품 이름")
        private String itemName;
        @Schema(description = "주문 수량")
        private int quantity;
        @Schema(description = "상품 단가")
        private int price;
        @Schema(description = "해당 상품 총합 금액 (단가 * 수량 + 옵션가)")
        private int totalPrice;
        @Schema(description = "상품에 대해 선택된 옵션 내역")
        private List<Option> selectedOptions;
    }

    @Getter @Builder @AllArgsConstructor
    @Schema(description = "선택된 상품 옵션 그룹")
    public static class Option {
        @Schema(description = "옵션 그룹 이름")
        private String optionName;
        @Schema(description = "옵션 기본 가격")
        private int optionPrice;
        @Schema(description = "해당 그룹 내 선택된 하위 상세 옵션")
        private List<SubOption> subOptions;
    }

    @Getter @Builder @AllArgsConstructor
    @Schema(description = "선택된 하위 상세 옵션")
    public static class SubOption {
        @Schema(description = "하위 옵션 항목 이름")
        private String name;
        @Schema(description = "추가 가격")
        private int addPrice;
    }
}