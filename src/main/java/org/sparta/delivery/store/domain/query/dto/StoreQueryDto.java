package org.sparta.delivery.store.domain.query.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * 1. 일반 사용자는 영업준비중(PREPARING), 영업중(OPEN) 매장 상태를 조회, 매장 주인 사용자는 자신의 가게는 모든 상태 조회 가능, 관리자는 제한 없음
 * 2. sigugun은 단일로 조회는 불가, 반드시 sido + sigugun 조건으로만 조회 가능
 * 3. sigugun은 복수개 선택 가능하고 OR 조건으로 조회
 * 4. sido는 단일 조회 가능
 * 5. keyword는 매장명, 매장 전화번호, 이메일 중에 키워드가 포함되었는지 체크
 */
public class StoreQueryDto {
    @Getter
    @Builder
    public static class Search {
        private List<UUID> categoryId; // 매장 분류
        private String storeName;
        private String storeStatus; // 매장 상태
        private String storeContact; // 매장 전화번호, 이메일
        private String sido; // 시도
        private List<String> sigugun; // 시구군
        private String keyword; // 키워드
    }
}
