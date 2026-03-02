package org.sparta.delivery.store.domain.query;

import org.sparta.delivery.store.domain.Product;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.query.dto.ProductQueryDto;

import java.util.List;
import java.util.Optional;

/**
 * 1. 상품 조회는 매장에 속해있는 상품만 가능하며, 준비중 상태는 노출하지 않습니다.
 * 2. 목록 조회(findAll)에서는 모든 목록을 확인해야 하므로 페이징은 하지 않습니다.
 * 3. 목록 조회에서 categoryIds는 IN 조건으로 조회하며, keyword는 상품명(name), 상품코드(productCode)에서 키워드 검색을 한다.
 * 4. productCodes는 복수개 조회 가능하야 하므로 IN 조건으로 조회
 */
public interface ProductQueryRepository {
    Optional<Product> findByProductCode(StoreId id, String productCode);
    List<Product> findAll(StoreId id, ProductQueryDto.Search search);
}
