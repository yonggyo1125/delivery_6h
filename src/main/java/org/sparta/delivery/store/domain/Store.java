package org.sparta.delivery.store.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.BaseUserEntity;
import org.sparta.delivery.global.domain.service.AddressToCoords;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.global.presentation.exception.UnAuthorizedException;
import org.sparta.delivery.store.domain.exception.ProductNotFoundException;
import org.sparta.delivery.store.domain.service.OwnerCheck;

import java.util.*;

/**
 * 모든 기능은 매장 주인(OWNER)와 관리자(MANAGER, MASTER)만 가능
 * 상품 수정 및 삭제시 매장 주인인 경우는 자신의 매장만, 관리자는 모두 가능
 * 분류 등록, 수정 시 유효한 분류인지 체크
 * 매장을 등록하면 기본 상태는 오픈 중비중 상태
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

    @Column(length=30)
    @Enumerated(EnumType.STRING)
    private StoreStatus status; // 매장 운영 상태

    @Embedded
    private Owner owner;

    @Column(length=65, name="store_name")
    private String name; // 매장명

    @Embedded
    private StoreContact contact; // 매장 연락처

    @Embedded
    private StoreLocation location; // 매장 위치

    // 운영 요일 및 시간 - 1:N 관계 - 운영 요일 및 시간이 등록되지 않는다면 연중 무휴
    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name="P_STORE_OPERATION", joinColumns=@JoinColumn(name="store_id"))
    @OrderColumn(name="operation_idx")
    private List<StoreOperation> operations;

    // 매장 분류 - 1:N 관계
    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name="P_STORE_CATEGORY", joinColumns=@JoinColumn(name="store_id"))
    @OrderColumn(name="category_idx")
    private List<StoreCategory> categories;

    // 매장 메뉴 - 1:N 관계
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "P_PRODUCT", joinColumns = @JoinColumn(name="store_id"))
    @OrderColumn(name="product_idx")
    private List<Product> products;


    @Builder
    public Store(UUID storeId, UUID ownerId, String ownerName, String landline, String email, String address, List<UUID> categoryIds, AddressToCoords addressToCoords, RoleCheck roleCheck, OwnerCheck ownerCheck) {

        // 등록 권한 체크
        checkPossible(roleCheck, ownerCheck);

        this.id = storeId == null ? StoreId.of() : StoreId.of(storeId);
        this.owner = new Owner(ownerId, ownerName);
        this.contact = new StoreContact(landline, email);
        this.location = new StoreLocation(address, addressToCoords);
        this.status = StoreStatus.PREPARING;

        // 분류 추가
        createCategory(roleCheck, ownerCheck, categoryIds);
    }

    ////  상품 S
    // 상품 추가
    public void createProduct(RoleCheck roleCheck, OwnerCheck ownerCheck, UUID categoryId, String name, int price, List<ProductOption> options) {
        // 권한 체크
        checkPossible(roleCheck, ownerCheck);
        products = Objects.requireNonNullElseGet(products, ArrayList::new);

        products.add(Product.builder()
                        .categoryId(categoryId)
                        .name(name)
                        .price(price)
                        .options(options)
                    .build());
    }

    // 상품 수정
    public void updateProduct(RoleCheck roleCheck, OwnerCheck ownerCheck, int productIdx, UUID categoryId, String name, int price, List<ProductOption> options ) {
        // 권한 체크
        checkPossible(roleCheck, ownerCheck);
        if (products == null || products.get(productIdx) == null) {
            throw new ProductNotFoundException();
        }

        products.set(productIdx, Product.builder()
                        .categoryId(categoryId)
                        .name(name)
                        .price(price)
                        .options(options)
                .build());
    }

    // 상품 삭제
    public void removeProduct(RoleCheck roleCheck, OwnerCheck ownerCheck, List<Integer> productIdxes) {
        checkPossible(roleCheck, ownerCheck);
        if (products == null) return;

        List<Product> newProducts = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            if (!productIdxes.contains(i)) {
                newProducts.add(products.get(i));
            }
        }

        products = newProducts;
    }
    ////  상품 E

    ///// 카테고리 S
    // 카테고리 추가
    public void createCategory(RoleCheck roleCheck, OwnerCheck ownerCheck, List<UUID> categoryIds) {
        // 권한 체크
        checkPossible(roleCheck, ownerCheck);
        if (categoryIds == null || categoryIds.isEmpty()) return;

        categories = Objects.requireNonNullElseGet(categories, ArrayList::new);
        categories.addAll(categoryIds.stream().distinct().map(StoreCategory::new).toList());
    }

    public void createCategory(RoleCheck roleCheck, OwnerCheck ownerCheck, UUID categoryId) {
        createCategory(roleCheck, ownerCheck, List.of(categoryId));
    }

    // 카테고리 모두 지우기
    public void truncateCategory(RoleCheck roleCheck, OwnerCheck ownerCheck) {
        // 권한 체크
        checkPossible(roleCheck, ownerCheck);
        categories = null;
    }

    // 카테고리 교체
    public void replaceCategory(RoleCheck roleCheck, OwnerCheck ownerCheck, List<UUID> categoryIds) {
        truncateCategory(roleCheck,ownerCheck);
        createCategory(roleCheck, ownerCheck, categoryIds);
    }

    // 카테고리 제거
    public void removeCategory(RoleCheck roleCheck, OwnerCheck ownerCheck, List<UUID> categoryIds) {
        // 권한 체크
        checkPossible(roleCheck, ownerCheck);
        if (categories == null) return;

        categories = categories.stream().filter(c -> !categoryIds.contains(c.getCategoryId())).toList();
    }
    ///// 카테고리 E


    /**
     * 모든 기능은 매장 주인(OWNER)와 관리자(MANAGER, MASTER)만 가능
     * storeId가 null 이라면 신규 등록이므로 ONWER 권한이 있는지만 체크,
     *          null이 아니라면 storeId로 매장의 소유자인지 체크
     */
    private void checkPossible(RoleCheck roleCheck, OwnerCheck ownerCheck) {

        if (!roleCheck.hasRole(List.of("MASTER", "MASTER")) && !ownerCheck.isOwner(id.getId())) {
            throw new UnAuthorizedException();
        }
    }
}
