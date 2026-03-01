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
import org.sparta.delivery.store.domain.StoreStatus;
import org.sparta.delivery.store.domain.query.StoreQueryRepository;
import org.sparta.delivery.store.domain.query.dto.StoreQueryDto;
import org.sparta.delivery.store.domain.service.OwnerCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
         * 1. 일반 사용자는 영업준비중(PREPARING), 영업중(OPEN) 매장 상태를 조회, 매장 주인 사용자는 자신의 가게는 모든 상태 조회 가능, 관리자는 제한 없음
         * 2. sigugun은 단일로 조회는 불가, 반드시 sido + sigugun 조건으로만 조회 가능
         * 3. sigugun은 복수개 선택 가능하고 OR 조건으로 조회
         * 3. sido는 단일 조회 가능
         */
        QStore store = QStore.store;
        BooleanBuilder andBuilder = new BooleanBuilder();

        // 매장 상태 권한 처리
        // 관리자(MANAGER, MASTER)는 모든 상태 조회 가능하므로 필터링 생략
        if (!roleCheck.hasRole(List.of("MANAGER", "MASTER"))) {
            BooleanBuilder statusRoleBuilder = new BooleanBuilder();
            if (roleCheck.hasRole("OWNER")) {
                // 매장 주인(OWNER): 내 가게는 모두 보이게 + 다른 사람 가게는 영업중/준비중만 보이게
                statusRoleBuilder.and(
                        store.owner.id.eq(ownerCheck.getOwnerId())
                        .or(store.status.in(StoreStatus.PREPARING, StoreStatus.OPEN)));
            } else {
                // 일반 사용자: 영업중/준비중 상태만 노출
                statusRoleBuilder.and(store.status.in(StoreStatus.PREPARING, StoreStatus.OPEN));
            }

            andBuilder.and(statusRoleBuilder);
        }

        // 지역 조건(sido + sigugun 또는 sido 단일 조회) 처리
        String sido = search.getSido();
        List<String> sigugun = search.getSigugun();
        if (StringUtils.hasText(sido)) {
            if (sigugun != null && !sigugun.isEmpty()) {
                // sido + sigugun // sigugun은 or 조건으로 다중 검색
                BooleanBuilder areaBuilder = new BooleanBuilder();
                sigugun.forEach(s -> areaBuilder.or(store.location.address.startsWith(sido + " " + s)));
                andBuilder.and(areaBuilder);
            } else { // sido 단일 조회
                andBuilder.and(store.location.address.startsWith(sido));
            }
        }

        // 매장명 처리
        String storeName = search.getStoreName();
        if (StringUtils.hasText(storeName)) {
            andBuilder.and(store.name.containsIgnoreCase(storeName));
        }

        // 연락처 처리 - 전화번호, 이메일
        String storeContact = search.getStoreContact();
        if (StringUtils.hasText(storeContact)) {
            andBuilder.and(store.contact.landline.containsIgnoreCase(storeContact)
                    .or(store.contact.email.containsIgnoreCase(storeContact)));
        }

        // 카테고리 처리
        List<UUID> categoryIds = search.getCategoryId();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            andBuilder.and(store.categories.any().categoryId.in(categoryIds));
        }

        // 키워드 처리 - 매장명, 매장 전화번호, 이메일 중에 키워드가 포함되었는지 체크
        String keyword = search.getKeyword();
        if (StringUtils.hasText(keyword)) {
            andBuilder.and(
                        store.name.containsIgnoreCase(keyword)
                        .or(store.contact.email.containsIgnoreCase(keyword))
                        .or(store.contact.landline.containsIgnoreCase(keyword))
            );
        }

        // 데이터 조회
        List<Store> items = queryFactory
                .selectFrom(store)
                .where(andBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(store.createdAt.desc())
                .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(store.count())
                .from(store)
                .where(andBuilder);

        return PageableExecutionUtils.getPage(items, pageable, countQuery::fetchOne);
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
