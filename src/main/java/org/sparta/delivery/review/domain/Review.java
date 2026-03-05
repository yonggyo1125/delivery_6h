package org.sparta.delivery.review.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.sparta.delivery.global.domain.BaseUserEntity;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.global.domain.service.UserDetails;
import org.sparta.delivery.review.domain.exception.InvalidOrderStateForReviewException;
import org.sparta.delivery.review.domain.exception.ReviewAuthorityException;
import org.sparta.delivery.review.domain.service.OrderInfoProvider;
import org.sparta.delivery.review.domain.service.ReviewerCheck;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 1. 리뷰 작성시 주문자와 로그인한 사용자가 같은지 체크
 * 2. 리뷰 수정인 경우 리뷰 작성자가 로그인한 사용자와 같은지도 체크
 *    (주문번호는 최초 등록시에만 수정이 되므로 수정일땐 체크 불필요)
 *    관리자(MANAGER, MASTER)는 권한 체크 필요없이 항상 가능(추가, 수정)
 * 3.리뷰는 주문상태가 완료(ORDER_DONE)로 변경이 되면(최종 주문상태) 리뷰를 작성할 수 있다.
 * 4. 리뷰는 주문을 한 사용자만 작성 가능
 * 5. 리뷰의 평점은 필수이며 1~5점 사이 선택
 * 6. 리뷰 작성 또는 수정이 완료되면 평점에대한 평균을 주문에 해당하는 상점의 평점에 업데이트 합니다(이벤트 발행)
 */
@Entity
@ToString
@Getter
@Access(AccessType.FIELD)
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseUserEntity {

    @EmbeddedId
    private ReviewId id;

    @Embedded
    private Reviewer reviewer;

    @Embedded
    private ReviewOrderInfo info; // 주문 정보

    @Embedded
    private ReviewContent content; // 리뷰 내용

    @Builder
    public Review(UUID orderId, String subject, String content, int score, OrderInfoProvider orderInfoProvider, RoleCheck rolecheck, ReviewerCheck reviewerCheck, UserDetails userDetails) {
        // 권한 체크
        checkAuthority(orderId, reviewerCheck, rolecheck);

        // 작성 가능한 리뷰인지 체크 - 주문 존재 여부 및 상태 확인(ORDER_DONE)
        ReviewOrderInfo orderInfo = orderInfoProvider.getOrderInfo(orderId);
        if (orderInfo == null) {
            throw new InvalidOrderStateForReviewException(orderId);
        }

        this.id = ReviewId.of();
        this.reviewer = new Reviewer(userDetails); // 리뷰 작성자(로그인 정보에서 자동 완성)

        this.info = orderInfo; // 주문 정보
        this.content = new ReviewContent(subject, content, score); // 리뷰 내용
    }

    // 리뷰 수정
    public void change(String subject, String content, int rate, ReviewerCheck reviewerCheck, RoleCheck roleCheck) {
        // 권한 체크
        checkAuthority(info.getOrderId(), reviewerCheck, roleCheck);

        this.content = new ReviewContent(subject, content, rate);
    }

    // 리뷰 삭제(Soft Delete)
    public void remove(ReviewerCheck reviewerCheck, RoleCheck roleCheck) {
        // 권한 체크
        checkAuthority(info.getOrderId(), reviewerCheck, roleCheck);

        deletedAt = LocalDateTime.now();
    }

    /**
     * 1. 리뷰 작성시 주문자와 로그인한 사용자가 같은지 체크
     * 2. 리뷰 수정인 경우 리뷰 작성자가 로그인한 사용자와 같은지도 체크
     *    (주문번호는 최초 등록시에만 수정이 되므로 수정일땐 체크 불필요)
     * 3. 관리자(MANAGER, MASTER)는 권한 체크 필요없이 항상 가능
     */
    private void checkAuthority(UUID orderId, ReviewerCheck reviewerCheck, RoleCheck roleCheck) {
        if (roleCheck.hasRole(List.of("MASTER", "MANAGER"))) {
            return;
        }

        if (!reviewerCheck.check(id, orderId)) {
            if (id == null) { // 새로 작성한 경우
                throw new ReviewAuthorityException(orderId);
            } else {
                throw new ReviewAuthorityException();
            }
        }
    }
}
