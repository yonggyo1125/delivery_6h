package org.sparta.delivery.store.application.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.review.domain.event.ReviewScoreChangedEvent;
import org.sparta.delivery.store.domain.Store;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.StoreRepository;
import org.sparta.delivery.store.domain.exception.StoreNotFoundException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

// 후기 작성, 수정, 삭제시 상점별 리뷰 평점 업데이트 처리 이벤트 핸들러
@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewScoreChangedEventHandler {
    private final StoreRepository storeRepository;

    @Async
    @Retryable(
            retryFor = { Exception.class },
            noRetryFor = { StoreNotFoundException.class }, // 매장이 없다면 재시도 필요 없음
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2.0)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReviewScoreChangedEvent event) {
        Store store = getStore(event.storeId());
        store.systemUpdateReviewScore(event.averageScore());
    }

    private Store getStore(UUID storeId) {
        return storeRepository.findById(StoreId.of(storeId)).orElseThrow(StoreNotFoundException::new);
    }

    /**
     * 리뷰 평점 평균 업데이트는 최종 실패하더라도 다음 리뷰 작성시 재 업데이트 하면 되므로
     * 후속처리는 따로 하지 않는다. 다만 로그는 기록하여 실패 사유를 관리자 차원에서 확인 할수 있도록 한다.
     */
    @Recover
    public void completeFailure(Exception e, ReviewScoreChangedEvent event) {
        log.error("매장별 리뷰 평점 평균 변경 최종 실패. 사유: {}. 매장ID: {}, 평점: {}", e.getMessage(), event.storeId(), event.averageScore(), e);
    }
}
