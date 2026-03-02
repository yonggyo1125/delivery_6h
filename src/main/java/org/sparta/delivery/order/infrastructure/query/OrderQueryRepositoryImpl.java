package org.sparta.delivery.order.infrastructure.query;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.OwnerCheck;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.global.domain.service.UserDetails;
import org.sparta.delivery.order.domain.Order;
import org.sparta.delivery.order.domain.OrderId;
import org.sparta.delivery.order.domain.OrderStatus;
import org.sparta.delivery.order.domain.QOrder;
import org.sparta.delivery.order.domain.query.OrderQueryDto;
import org.sparta.delivery.order.domain.query.OrderQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.sparta.delivery.order.domain.QOrder.order;



@Repository
@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final RoleCheck roleCheck;
    private final OwnerCheck ownerCheck;
    private final UserDetails userDetails;

    // 자신의 주문건(사용자), 또는 매장 점주의 주문건으로 한정, 관리자의 경우는 제한 없음
    @Override
    public Optional<Order> findById(OrderId orderId) {
        BooleanBuilder builder = new BooleanBuilder();

        // 주문 ID 및 삭제되지 않은 주문
        builder.and(order.id.eq(orderId));
        builder.and(order.deletedAt.isNull());

        // 권한 조건 분기 (OR 조건으로 결합)
        // 관리자(MASTER, MANAGER)는 모든 주문 조회 가능
        if (!roleCheck.hasRole(List.of("MASTER", "MANAGER"))) {
            BooleanBuilder authBuilder = new BooleanBuilder();

            // 내 주문이거나 (사용자 권한)
            authBuilder.or(order.orderer.id.eq(userDetails.getId()));

            // 내가 운영하는 매장의 주문이거나 (점주 권한)
            UUID myStoreId = ownerCheck.getStoreId();
            if (myStoreId != null) {
                authBuilder.or(order.storeInfo.storeId.eq(myStoreId));
            }

            builder.and(authBuilder);
        }

        return Optional.ofNullable(
                queryFactory.selectFrom(order)
                        .where(builder)
                        .fetchOne()
        );
    }

    @Override
    public Page<Order> findAllByStore(UUID storeId, OrderQueryDto.Search search, Pageable pageable) {
        return getOrders(search, pageable, order.storeInfo.storeId.eq(storeId));
    }

    @Override
    public Page<Order> findAllByUser(UUID userId, OrderQueryDto.Search search, Pageable pageable) {
        return getOrders(search, pageable, order.orderer.id.eq(userId));
    }

    @Override
    public Page<Order> findAll(OrderQueryDto.Search search, Pageable pageable) {
        return getOrders(search, pageable, null);
    }

    private Page<Order> getOrders(OrderQueryDto.Search search, Pageable pageable, Predicate addCondition) {
        QOrder order = QOrder.order;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(order.deletedAt.isNull()); // 삭제된 주문은 미노출
        if (addCondition != null) {
            builder.and(addCondition); // 추가 조건 처리
        }

        // 검색 조건 처리
        if (search != null) {
            if (search.getOrderIds() != null && !search.getOrderIds().isEmpty()) {
                builder.and(order.id.id.in(search.getOrderIds()));
            }
            if (StringUtils.hasText(search.getOrdererName())) {
                builder.and(order.orderer.name.contains(search.getOrdererName()));
            }
            if (StringUtils.hasText(search.getOrdererMobile())) {
                builder.and(order.orderer.mobile.eq(search.getOrdererMobile()));
            }
            if (StringUtils.hasText(search.getOrdererEmail())) {
                builder.and(order.orderer.email.eq(search.getOrdererEmail()));
            }
            if (search.getStoreIds() != null && !search.getStoreIds().isEmpty()) {
                builder.and(order.storeInfo.storeId.in(search.getStoreIds()));
            }
            if (StringUtils.hasText(search.getStoreName())) {
                builder.and(order.storeInfo.storeName.contains(search.getStoreName()));
            }
            if (StringUtils.hasText(search.getStoreAddress())) {
                builder.and(order.storeInfo.storeAddress.contains(search.getStoreAddress()));
            }
            if (StringUtils.hasText(search.getStoreTel())) {
                builder.and(order.storeInfo.storeTel.eq(search.getStoreTel()));
            }
            if (StringUtils.hasText(search.getDeliveryAddress())) {
                builder.and(order.deliveryInfo.address.contains(search.getDeliveryAddress()));
            }
            if (search.getOrderStatuses() != null && !search.getOrderStatuses().isEmpty()) {
                builder.and(order.status.in(search.getOrderStatuses().stream().map(OrderStatus::valueOf).toList()));
            }
        }

        List<Order> items = queryFactory
                .selectFrom(order)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .where(builder);

        return PageableExecutionUtils.getPage(items, pageable, countQuery::fetchOne);
    }
}
