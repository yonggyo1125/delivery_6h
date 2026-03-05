package org.sparta.delivery.review.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import org.sparta.delivery.global.domain.BaseUserEntity;

/**
 * 1.리뷰는 주문상태가 완료(ORDER_DONE)로 변경이 되면(최종 주문상태) 리뷰를 작성할 수 있다.
 * 2. 리뷰는 주문을 한 사용자만 작성 가능
 * 3. 리뷰의 평점은 필수이며 1~5점 사이 선택
 * 4. 리뷰 작성이 완료되면 평점에대한 평균을 주문에 해당하는 상점의 평점에 업데이트 합니다(이벤트 발행)
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
    private ReviewOrderInfo info; // 주문 정보

    private ReviewContent content; // 리뷰 내용
}
