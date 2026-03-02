package org.sparta.delivery.store.infrastructure.query;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.store.domain.Product;
import org.sparta.delivery.store.domain.ProductStatus;
import org.sparta.delivery.store.domain.QProduct;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.query.ProductQueryRepository;
import org.sparta.delivery.store.domain.query.dto.ProductQueryDto;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 1. 상품 조회는 매장에 속해있는 상품만 가능하며, 준비중 상태는 노출하지 않습니다.
 * 2. 목록 조회(findAll)에서는 모든 목록을 확인해야 하므로 페이징은 하지 않습니다.
 * 3. 목록 조회에서 categoryIds는 IN 조건으로 조회하며, keyword는 상품명(name), 상품코드(productCode)에서 키워드 검색을 한다.
 * 4. productCodes는 복수개 조회 가능하야 하므로 IN 조건으로 조회
 */
@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Product> findByProductCode(StoreId id, String productCode) {
        QProduct product = QProduct.product;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(product)
                        .where(
                                product.id.storeId.eq(id),
                                product.productCode.eq(productCode),
                                product.status.ne(ProductStatus.READY), // 상품 준비중은 미노출
                                product.deletedAt.isNull() // 미삭제된 상품만 조회
                        )
                        .fetchOne()
        );
    }

    @Override
    public List<Product> findAll(StoreId id, ProductQueryDto.Search search) {
        QProduct product = QProduct.product;
        BooleanBuilder andBuilder = new BooleanBuilder();

        // 매장 일치 + 준비중 제외 + 삭제 제외
        andBuilder.and(product.id.storeId.eq(id))
                .and(product.status.ne(ProductStatus.READY))
                .and(product.deletedAt.isNull());

        // 카테고리 처리
        List<UUID> categoryIds = search.getCategoryIds();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            andBuilder.and(product.category.in(categoryIds));
        }

        // 상품코드 처리
        List<String> productCodes = search.getProductCodes();
        if (productCodes != null && !productCodes.isEmpty()) {
            andBuilder.and(product.productCode.in(productCodes));
        }

        // 상품명 검색
        String name = search.getName();
        if (StringUtils.hasText(name)) {
            andBuilder.and(product.name.containsIgnoreCase(name));
        }

        // 키워드 검색(상품명 + 상품 코드)
        String keyword = search.getKeyword();
        if (StringUtils.hasText(keyword)) {
            andBuilder.and(product.name.containsIgnoreCase(keyword)
                    .or(product.productCode.containsIgnoreCase(keyword)));
        }

        return queryFactory
                .selectFrom(product)
                .leftJoin(product.options)
                .fetchJoin()
                .distinct()
                .where(andBuilder)
                .orderBy(product.createdAt.desc())
                .fetch();
    }
}
