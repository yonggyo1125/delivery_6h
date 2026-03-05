package org.sparta.delivery.review.infrastructure.query;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.review.domain.Review;
import org.sparta.delivery.review.domain.ReviewId;
import org.sparta.delivery.review.domain.query.ReviewQueryDto;
import org.sparta.delivery.review.domain.query.ReviewQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.sparta.delivery.review.domain.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Review> findById(ReviewId reviewId) {
       return Optional.ofNullable(
                queryFactory.selectFrom(review)
                        .where(review.id.eq(reviewId), isNotDeleted())
                        .fetchOne());
    }

    @Override
    public Page<Review> findAllByStore(UUID storeId, ReviewQueryDto.Search search, Pageable pageable) {
        BooleanBuilder builder = createBuilder(search);
        builder.and(review.info.storeId.eq(storeId));

        return getPage(builder, pageable);
    }

    @Override
    public Page<Review> findAllByUser(UUID userId, ReviewQueryDto.Search search, Pageable pageable) {
        BooleanBuilder builder = createBuilder(search);
        builder.and(review.reviewer.id.eq(userId));

        return getPage(builder, pageable);
    }

    @Override
    public Page<Review> findAll(ReviewQueryDto.Search search, Pageable pageable) {
        return getPage(createBuilder(search), pageable);
    }

    private Page<Review> getPage(BooleanBuilder builder, Pageable pageable) {
        builder.and(isNotDeleted());

        List<Review> items = queryFactory
                .selectFrom(review)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(review.createdAt.desc())
                .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(builder);


        return PageableExecutionUtils.getPage(items, pageable, countQuery::fetchOne);
    }

    private BooleanExpression isNotDeleted() {
        return review.deletedAt.isNull();
    }


    private BooleanBuilder createBuilder(ReviewQueryDto.Search search) {
        BooleanBuilder builder = new BooleanBuilder();
        if (search == null) return builder;

        if (StringUtils.hasText(search.getKeyword())) {
            ReviewQueryDto.SearchOption option = Objects.requireNonNullElse(search.getOption(), ReviewQueryDto.SearchOption.ALL); // option이 null 이면 기본값 ALL
            builder.and(getSearchCondition(option, search.getKeyword()));
        }

        if (search.getStoreIds() != null && !search.getStoreIds().isEmpty()) {
            builder.and(review.info.storeId.in(search.getStoreIds()));
        }

        if (search.getOrderIds() != null && !search.getOrderIds().isEmpty()) {
            builder.and(review.info.orderId.in(search.getOrderIds()));
        }

        return builder;
    }

    private BooleanExpression getSearchCondition(ReviewQueryDto.SearchOption option, String keyword) {
        if (option == null) return null;

        return switch (option) {
            case SUBJECT -> review.content.subject.containsIgnoreCase(keyword);
            case CONTENT -> review.content.content.containsIgnoreCase(keyword);
            case REVIEWER -> review.reviewer.reviewerName.containsIgnoreCase(keyword);
            case STORE_NAME -> review.info.storeName.containsIgnoreCase(keyword);
            case SUBJECT_CONTENT -> review.content.subject.containsIgnoreCase(keyword)
                    .or(review.content.content.containsIgnoreCase(keyword));
            case ALL -> review.content.subject.containsIgnoreCase(keyword)
                    .or(review.content.content.containsIgnoreCase(keyword))
                    .or(review.reviewer.reviewerName.containsIgnoreCase(keyword))
                    .or(review.info.storeName.containsIgnoreCase(keyword));
        };
    }
}
