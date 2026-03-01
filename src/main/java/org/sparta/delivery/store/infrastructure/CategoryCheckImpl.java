package org.sparta.delivery.store.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.category.domain.QCategory;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.StoreRepository;
import org.sparta.delivery.store.domain.service.CategoryCheck;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryCheckImpl implements CategoryCheck {
    private final JPAQueryFactory queryFactory;
    private final StoreRepository storeRepository;

    @Override
    // 매장에 카테고리 등록,수정 요청시 모든 카테고리가 존재하는지 체크
    public boolean exists(List<UUID> categoryIds) {
        QCategory category = QCategory.category;
        if (categoryIds == null || categoryIds.isEmpty()) return true;
        List<UUID> ids = categoryIds.stream().distinct().toList();
        long count = Objects.requireNonNullElse(queryFactory
                .select(category.count())
                .from(category)
                .where(category.id.id.in(categoryIds))
                .fetchOne(), 0L);

        return count == ids.size();
    }

    @Override
    // 상품등록,수정시 매장이 가지고 있는 분류인지 체크
    public boolean existsInStore(StoreId storeId, UUID categoryId) {
        return storeRepository.existsByStoreIdAndCategoryId(storeId, categoryId);
    }
}
