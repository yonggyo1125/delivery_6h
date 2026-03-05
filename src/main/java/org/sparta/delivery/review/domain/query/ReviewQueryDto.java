package org.sparta.delivery.review.domain.query;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewQueryDto {
    @Getter
    @Builder
    public static class Search {
        private SearchOption option;
        private String keyword; // option 바탕으로 키워드 검색
        private List<UUID> storeIds; // 매장 아이디(복수개)로 조회
        private List<UUID> orderIds; // 주문 번호(복수개)로 조회
    }

    public enum SearchOption {
        SUBJECT, // 제목 조회
        CONTENT, // 내용 조회
        REVIEWER, // 리뷰 작성자 조회
        STORE_NAME, // 매장명으로 조회
        SUBJECT_CONTENT, // 제목 + 내용 함께 조회
        ALL, // 제목 + 내용 + 리뷰 작성자 + 매장명 조회
    }
}
