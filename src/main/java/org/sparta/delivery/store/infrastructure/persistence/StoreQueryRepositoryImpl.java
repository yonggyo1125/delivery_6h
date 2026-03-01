package org.sparta.delivery.store.infrastructure.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.store.domain.QStore;
import org.sparta.delivery.store.domain.Store;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.query.StoreQueryRepository;
import org.sparta.delivery.store.domain.StoreStatus;
import org.sparta.delivery.store.domain.query.dto.StoreQueryDto;
import org.sparta.delivery.store.domain.service.OwnerCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StoreQueryRepositoryImpl implements StoreQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final RoleCheck roleCheck;
    private final OwnerCheck ownerCheck;

    @Override
    public Optional<Store> findById(StoreId id) {
        QStore store = QStore.store;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(store)
                        .where(store.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public Page<Store> findAll(StoreQueryDto.Search search, Pageable pageable) {
        /**
         * 1. 일반 사용자는 영업준비중(READY), 영업중(OPEN) 매장 상태를 조회, 매장 주인 사용자는 자신의 가게는 모든 상태 조회 가능, 관리자는 제한 없음
         * 2. sigugun은 단일로 조회는 불가, 반드시 sido + sigugun 조건으로만 조회 가능
         * 3. sigugun은 복수개 선택 가능하고 OR 조건으로 조회
         * 3. sido는 단일 조회 가능
         */
        QStore store = QStore.store;



        return null;
    }

    // 현재 좌표에서 몇 km 반경에 가장 가까운 매장 조회
    @Override
    public Page<Store> findAllNearest(double latitude, double longitude, double radiusKm, Pageable pageable) {

        QStore store = QStore.store;

        if (radiusKm < 0.0) radiusKm = 3.0;

        // PostGIS ST_DistanceSphere 함수를 사용한 거리 계산 (단위: 미터)
        // POINT(longitude latitude) 순서로 문자열 생성
        String userLocation = "POINT(%.10f %.10f)".formatted(longitude, latitude);

        NumberExpression<Double> distanceMeter = Expressions.numberTemplate(Double.class, "ST_DistanceSphere({0}, ST_GeomFromText({1}, 4326))", store.location.point, userLocation);

        // 좌표에서 가까운 매장 조회
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(store.status.in(StoreStatus.PREPARING, StoreStatus.OPEN)) // 영업 준비중, 운영중 업체만
                .and(distanceMeter.loe(radiusKm * 1000));  // 반경 N km 이내
        List<Store> items = queryFactory
                .selectFrom(store)
                .where(andBuilder)
                .orderBy(distanceMeter.asc()) // 가까운 순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 검색 총 갯수 조회(페이징 목적)
        JPAQuery<Long> countQuery = queryFactory
                .select(store.count())
                .from(store)
                .where(andBuilder);

        return PageableExecutionUtils.getPage(items, pageable, countQuery::fetchOne);
    }
}
