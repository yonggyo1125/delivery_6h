package org.sparta.delivery.order.domain.query;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderQueryDto {
    @Getter
    @Builder
    public static class Search {
        List<UUID> orderIds; // 주문번호는 단일 또는 복수개 검색 가능
        String ordererName; // 주문자명
        String ordererTel; // 주문자 전화번호
        String ordererEmail;  // 주문자 이메일
        List<UUID> storeIds; // 매장 ID 목록
        String storeName; // 매장명
        String storeAddress; // 매장 주소
        String storeTel; // 매장 전화번호
        String deliveryAddress; // 배송지 주소
        List<String> orderStatuses; // 주문 상태(복수개 검색 가능)

    }
}
