package org.sparta.delivery.store;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sparta.delivery.global.domain.BaseUserEntity;

import java.util.List;

/**
 * 모든 기능은 매장 주인(OWNER)와 관리자(MANAGER, MASTER)만 가능
 * 상품 수정 및 삭제시 매장 주인인 경우는 자신의 매장만, 관리자는 모두 가능
 * 분류 등록, 수정 시 유효한 분류인지 체크
 *
 */
@Entity
@ToString @Getter
@Table(name="P_STORE")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseUserEntity {
    @EmbeddedId
    private StoreId id;

    @Embedded
    private Owner owner;

    // 매장 분류 - 1:N 관계
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "P_STORE_CATEGORY", joinColumns = @JoinColumn(name="store_id"))
    @OrderColumn(name="category_idx")
    private List<StoreCategory> categories;

    // 매장 메뉴 - 1:N 관계
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "P_PRODUCT", joinColumns = @JoinColumn(name="store_id"))
    @OrderColumn(name="product_idx")
    private List<Product> products;
}
